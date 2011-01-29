package com.pyx4j.client.demo.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.client.demo.client.place.GoodbyePlace;
import com.pyx4j.client.demo.client.ui.SayHelloView;

public class SayHelloActivity extends AbstractActivity implements SayHelloView.Presenter {

    private final SayHelloView view;

    private final PlaceController placeController;

    @Inject
    public SayHelloActivity(SayHelloView view, PlaceController placeController) {
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
    public void sayGoodbye() {
        placeController.goTo(new GoodbyePlace("User"));
    }

}
