package com.jambacabs.driver.callbacks;

import org.json.JSONObject;

public interface IVehicles
{
    void onVehiclesClicked(JSONObject dict_element, int current_position, int previous_position,boolean isUpdateVehicle, boolean isApproved);
}
