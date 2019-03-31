package com.aurora.paperviewerprocessor.basicpluginobject;

import org.junit.Test;

public class BasicPluginObjectUnitTest {

    @Test
    public void BasicPluginObject_Gson(){
        BasicPluginObject originalBasicPluginObject = new BasicPluginObject();
        originalBasicPluginObject.setResult("Test");
        String jsonPluginObject = originalBasicPluginObject.toJSON();
        BasicPluginObject extractedBasicPluginObject = (BasicPluginObject) BasicPluginObject.fromJson(jsonPluginObject);
        assert (extractedBasicPluginObject.getResult().equals(originalBasicPluginObject.getResult()));
    }

}
