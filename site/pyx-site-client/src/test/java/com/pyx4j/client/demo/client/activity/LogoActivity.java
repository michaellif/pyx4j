package com.pyx4j.client.demo.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.client.demo.client.place.GoodbyePlace;
import com.pyx4j.client.demo.client.place.LandingPlace;
import com.pyx4j.client.demo.client.ui.LogoView;

public class LogoActivity extends AbstractActivity implements LogoView.Presenter {

    private final LogoView view;

    private final PlaceController placeController;

    @Inject
    public LogoActivity(LogoView view, PlaceController placeController) {
        this.view = view;
        this.placeController = placeController;
        view.setPresenter(this);
    }

    public LogoActivity withPlace(GoodbyePlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
    }

    @Override
    public void navigToLanding() {
        placeController.goTo(new LandingPlace());
    }

}