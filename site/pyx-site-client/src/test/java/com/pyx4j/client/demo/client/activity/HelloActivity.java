package com.pyx4j.client.demo.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.client.demo.client.place.HelloPlace;
import com.pyx4j.client.demo.client.ui.HelloView;

public class HelloActivity extends AbstractActivity implements Presenter {
    private String name;

    private final HelloView view;

    private final PlaceController placeController;

    @Inject
    public HelloActivity(HelloView view, PlaceController placeController) {
        this.view = view;
        this.placeController = placeController;
    }

    public HelloActivity withPlace(HelloPlace place) {
        this.name = place.getHelloName();
        return this;
    }

    /**
     * Invoked by the ActivityManager to start a new Activity
     */
    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setName(name);
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
    }

    /**
     * Ask user before stopping this activity
     */
    @Override
    public String mayStop() {
        return "Please hold on. This activity is stopping.";
    }

    /**
     * Navigate to a new Place in the browser
     */
    @Override
    public void goTo(Place place) {
        placeController.goTo(place);
    }
}
