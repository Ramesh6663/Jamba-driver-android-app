
package com.jambacabs.driver.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class ImageUploadBean {

    @Expose
    private String image;
    @Expose
    private String type;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
