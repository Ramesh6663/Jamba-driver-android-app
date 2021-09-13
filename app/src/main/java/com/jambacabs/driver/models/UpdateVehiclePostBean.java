
package com.jambacabs.driver.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class UpdateVehiclePostBean {

    @Expose
    private String id;
    @Expose
    private AddVehiclePostBean obj;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public AddVehiclePostBean getObj() {
        return obj;
    }

    public void setObj(AddVehiclePostBean obj) {
        this.obj = obj;
    }
}
