package com.jambacabs.driver.callbacks;

import com.google.android.libraries.places.api.model.Place;

public interface IPlaces
{
    void onClick(Place place, String area, String tag);
}
