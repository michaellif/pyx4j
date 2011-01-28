package com.pyx4j.client.demo.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

import com.pyx4j.client.demo.client.ui.GoodbyeView;
import com.pyx4j.client.demo.client.ui.HelloView;

public interface ClientFactory {
    EventBus getEventBus();

    PlaceController getPlaceController();

    HelloView getHelloView();

    GoodbyeView getGoodbyeView();
}
