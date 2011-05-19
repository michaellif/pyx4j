package com.pyx4j.client.demo.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.client.demo.client.place.AppPlace;
import com.pyx4j.client.demo.client.ui.MainContentView;

public class MainContentActivity extends AbstractActivity implements MainContentView.Presenter {

    private final MainContentView view;

    private String content;

    @Inject
    public MainContentActivity(MainContentView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public MainContentActivity withPlace(Place place) {
        setContent(place.toString() + ((AppPlace) place).getArgs());
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        view.setContent(content);
    }

}
