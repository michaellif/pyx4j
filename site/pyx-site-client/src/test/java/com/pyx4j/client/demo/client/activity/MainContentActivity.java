package com.pyx4j.client.demo.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.client.demo.client.ui.MainContentView;

public class MainContentActivity extends AbstractActivity implements MainContentView.Presenter {

    private final MainContentView view;

    private final PlaceController placeController;

    @Inject
    public MainContentActivity(MainContentView view, PlaceController placeController) {
        this.view = view;
        this.placeController = placeController;
        view.setPresenter(this);
    }

    public MainContentActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public String getContent() {
        Place place = placeController.getWhere();
        return place.toString();
    }

}
