package com.aurora.paperviewerenvironment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aurora.paperviewerprocessor.paper.Paper;
import com.ortiz.touchview.TouchImageView;

import java.util.List;

/**
 * A fragment containing either: <br>
 *     The view for the image gallery. <br>
 *     The view for the an enlarged image,
 *     encapsulated in a ViewPager to add scrolling behavior. <br>
 */
public class ImageFragment extends Fragment implements View.OnClickListener {

    /**
     * The {@link android.arch.lifecycle.AndroidViewModel}
     * for maintaining the paper it's data and state
     * across the lifecycles of the activity
     */
    private PaperViewModel mPaperViewModel;

    /**
     * The root view
     */
    private View mView;

    /**
     * The gallery for the images in the paper
     */
    private LinearLayout mImagesGallery;

    /**
     * The pager for the enlarged images
     */
    private ViewPager mEnlargedImagePager;

    /**
     * Button for returning from an enlarged image to the gallery view
     */
    private ImageButton mBtnMinimize;

    public ImageFragment() {
        // Empty constructor (generated by Android Studio)
    }

    /**
     * Returns a new instance of this fragment
     */
    public static ImageFragment newInstance() {
        return new ImageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPaperViewModel = ViewModelProviders.of(getActivity()).get(PaperViewModel.class);

        // Inflate the view for the image container
        mView = inflater.inflate(R.layout.fragment_image, container, false);
        mEnlargedImagePager = mView.findViewById(R.id.enlarged_image_pager);
        mBtnMinimize = mView.findViewById(R.id.btn_minimize_image);
        mBtnMinimize.setOnClickListener(this);

        mPaperViewModel.getPaper().observe(this, (Paper paper) -> {
            // Hide the enlarged image view
            mEnlargedImagePager.setVisibility(View.GONE);
            mBtnMinimize.setVisibility(View.GONE);

            // Add the images of the paper to the image gallery
            mImagesGallery = mView.findViewById(R.id.linear_image_gallery);
            mImagesGallery.setVisibility(View.VISIBLE);
            for (int i = 0; i < paper.getImages().size(); i++) {
                mImagesGallery.addView(getGalleryImageView(i, paper.getImages()));
            }
        });

        return mView;
    }

    /**
     * Creates an ImageView for in the image gallery.
     *
     * @param position The index of the image in the image gallery
     * @param images The images in the image gallery
     * @return {@link ImageView} containing the rescaled image for the gallery
     */
    private ImageView getGalleryImageView(int position, List<Bitmap> images){
        Bitmap bm = images.get(position);
        ImageView galleryImage = new ImageView(getActivity());
        int imageHeight = (int) getResources().getDimension(R.dimen.image_gallery_height);
        double ratio = ((double)bm.getWidth()/bm.getHeight());
        int galleryImageWidth = (int) (ratio * imageHeight);
        galleryImage.setLayoutParams(new ViewGroup.LayoutParams(galleryImageWidth, imageHeight));
        galleryImage.setImageBitmap(Bitmap.createScaledBitmap(bm, galleryImageWidth, imageHeight, false));

        float density = getResources().getDisplayMetrics().density;
        int paddingPixel = (int)(getResources().getDimension(R.dimen.gallery_padding) * density);
        galleryImage.setPadding(paddingPixel,0,paddingPixel,paddingPixel);

        // Set the click behavior for this gallery image
        galleryImage.setOnClickListener((View view) -> enLargeImage(position, images));
        return galleryImage;
    }

    /**
     * Creates a new view for an enlarged image. <br>
     * Triggered when an image is clicked in the image gallery.
     *
     * @param position The position of the image to enlarge in the {@link ViewPager}
     * @param images The images for in the {@link ViewPager}
     */
    public void enLargeImage(int position, List<Bitmap> images){
        mImagesGallery.setVisibility(View.GONE);

        // Initiate the ViewPager with the ImageAdapter with the clicked gallery image
        mBtnMinimize.setVisibility(View.VISIBLE);
        mEnlargedImagePager.setVisibility(View.VISIBLE);
        mEnlargedImagePager.setAdapter(new ImageAdapter(images));
        mEnlargedImagePager.setCurrentItem(position);
    }

    /**
     * A {@link PagerAdapter} that returns an ImageView
     * for the enlarged images when they are clicked in the image gallery.
     */
    public class ImageAdapter extends PagerAdapter {

        private List<Bitmap> mImages;

        public ImageAdapter(List<Bitmap> images){
            this.mImages = images;
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.pager_item, container, false);

            // Add the correct image to the inflated enlarged ImageView
            TouchImageView mEnlargedImageView = itemView.findViewById(R.id.imageView);
            mEnlargedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mEnlargedImageView.setAdjustViewBounds(true);
            mEnlargedImageView.setImageBitmap(mImages.get(position));

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    /**
     * Called when the minimization button has been clicked.
     *
     * @param view The root view
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_minimize_image:
                mEnlargedImagePager.setVisibility(View.GONE);
                mImagesGallery.setVisibility(View.VISIBLE);
                mBtnMinimize.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


}
