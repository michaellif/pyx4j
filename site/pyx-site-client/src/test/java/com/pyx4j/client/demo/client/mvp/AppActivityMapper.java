package com.pyx4j.client.demo.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.pyx4j.client.demo.client.ClientFactory;
import com.pyx4j.client.demo.client.activity.GoodbyeActivity;
import com.pyx4j.client.demo.client.activity.HelloActivity;
import com.pyx4j.client.demo.client.place.GoodbyePlace;
import com.pyx4j.client.demo.client.place.HelloPlace;

public class AppActivityMapper implements ActivityMapper {

    private final ClientFactory clientFactory;

    /**
     * AppActivityMapper associates each Place with its corresponding {@link Activity}
     * 
     * @param clientFactory
     *            Factory to be passed to activities
     */
    public AppActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    /**
     * Map each Place to its corresponding Activity. This would be a great use for GIN.
     */
    @Override
    public Activity getActivity(Place place) {
        // This is begging for GIN
        if (place instanceof HelloPlace)
            return new HelloActivity((HelloPlace) place, clientFactory);
        else if (place instanceof GoodbyePlace)
            return new GoodbyeActivity((GoodbyePlace) place, clientFactory);

        return null;
    }

}
