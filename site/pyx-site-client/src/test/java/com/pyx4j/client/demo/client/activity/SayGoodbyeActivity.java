package com.pyx4j.client.demo.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.client.demo.client.place.HelloPlace;
import com.pyx4j.client.demo.client.ui.SayGoodbyeView;

public class SayGoodbyeActivity extends AbstractActivity implements SayGoodbyeView.Presenter {

    private final SayGoodbyeView view;

    private final PlaceController placeController;

    @Inject
    public SayGoodbyeActivity(SayGoodbyeView view, PlaceController placeController) {
        super();
        this.view = view;
        this.placeController = placeController;
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

    }

    @Override
    public void sayHello() {
        placeController.goTo(new HelloPlace("User"));
    }

}
