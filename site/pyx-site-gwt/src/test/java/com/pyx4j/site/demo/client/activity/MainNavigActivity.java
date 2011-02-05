package com.pyx4j.site.demo.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.site.demo.client.ui.MainNavigView;

public class MainNavigActivity extends AbstractActivity implements MainNavigView.Presenter {

    private final MainNavigView view;

    private final PlaceController placeController;

    @Inject
    public MainNavigActivity(MainNavigView view, PlaceController placeController) {
        this.view = view;
        this.placeController = placeController;
        view.setPresenter(this);
    }

    public MainNavigActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void navigTo(Place place) {
        placeController.goTo(place);
    }

}
