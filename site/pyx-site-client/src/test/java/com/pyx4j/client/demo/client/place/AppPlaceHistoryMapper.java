package com.pyx4j.client.demo.client.place;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;

public class AppPlaceHistoryMapper implements PlaceHistoryMapper {

    private final AppPlaceListing listing;

    public AppPlaceHistoryMapper() {
        listing = GWT.create(AppPlaceListing.class);
    }

    @Override
    public Place getPlace(String token) {
        return listing.getPlace(token);
    }

    @Override
    public String getToken(Place place) {
        if (place != null) {
            return getToken(place.getClass());
        }
        return null;
    }

    public static String getToken(Class<? extends Place> clazz) {
        String simpleName = clazz.getName();
        // strip the package name
        return simpleName.substring(simpleName.lastIndexOf(".") + 1);
    }

}