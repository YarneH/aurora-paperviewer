package com.aurora.paperviewerenvironment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aurora.paperviewerprocessor.paper.Paper;

/**
 *
 */


/**
 * A fragment containing either: <br>
 *     The view for the image gallery. <br>
 *     The view for the an enlarged image,
 *     encapsulated in a ViewPager to add scrolling behavior. <br>
 */
public class ImageFragment extends Fragment {

    /**
     * The processed paper
     */
    private Paper mPaper;

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
     * Returns a new instance of this fragment
     */
    public static ImageFragment newInstance() {
        return new ImageFragment();
    }

    public void setPaper(Paper paper) {
        this.mPaper = paper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view for a section
        mView = inflater.inflate(R.layout.fragment_image, container, false);

        // Prepare the enlarged image view
        mEnlargedImagePager = mView.findViewById(R.id.enlarged_image_pager);
        mEnlargedImagePager.setVisibility(View.GONE);

        // Add the images of the paper to the image gallery
        mImagesGallery = mView.findViewById(R.id.linear_image_gallery);
        mImagesGallery.setVisibility(View.VISIBLE);
        for (int i = 0; i < mPaper.getImages().size(); i++) {
            mImagesGallery.addView(getGalleryImageView(i, mPaper.getImages().get(i)));
        }

        return mView;
    }

    /**
     * Creates an ImageView for in the image gallery.
     *
     * @param bm Bitmap with the image from the paper
     * @return An {@link ImageView} containing the rescaled image for the gallery
     */
    private ImageView getGalleryImageView(int position, Bitmap bm){
        ImageView galleryImage = new ImageView(getActivity());
        int imageHeight = (int) getResources().getDimension(R.dimen.image_gallery_height);
        double ratio = ((double)bm.getWidth()/bm.getHeight());
        int galleryImageWidth = (int) (ratio * imageHeight);
        galleryImage.setLayoutParams(new ViewGroup.LayoutParams(galleryImageWidth, imageHeight));
        galleryImage.setImageBitmap(Bitmap.createScaledBitmap(bm, galleryImageWidth, imageHeight, false));

        // Set the click behavior for this gallery image
        galleryImage.setOnClickListener(view -> enLargeImage(position));
        return galleryImage;
    }

    /**
     * Creates a new view for an enlarged image. <br>
     * Triggered when an image is clicked in the image gallery.
     *
     * @param position The position of the image to enlarge in the {@link ViewPager}
     */
    public void enLargeImage(int position){
        mImagesGallery.setVisibility(View.GONE);

        // Initiate the ViewPager with the ImageAdapter with the clicked gallery image
        mEnlargedImagePager.setVisibility(View.VISIBLE);
        mEnlargedImagePager.setAdapter(new ImageAdapter());
        mEnlargedImagePager.setCurrentItem(position);
    }

    /**
     * A {@link PagerAdapter} that returns an ImageView
     * for the enlarged images when they are clicked in the image gallery.
     */
    /**
     * Adapter for the {@link ViewPager} for enlarged images
     */
    public class ImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if(mPaper.getImages() != null){
                return mPaper.getImages().size();
            }
            return 0;
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
            ImageView mEnlargedImageView = itemView.findViewById(R.id.imageView);
            mEnlargedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mEnlargedImageView.setAdjustViewBounds(true);
            mEnlargedImageView.setImageBitmap(mPaper.getImages().get(position));

            // Set the behavior for clicking on an enlarged image
            itemView.setOnClickListener(view -> {
                mImagesGallery.setVisibility(View.VISIBLE);
                mEnlargedImagePager.setVisibility(View.GONE);
            });

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout)object);
        }
    }


}
