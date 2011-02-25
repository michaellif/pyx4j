package com.propertyvista.portal.tester.activity;

import java.util.Map;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.tester.ui.SignUpResultView;

import com.pyx4j.site.rpc.AppPlace;

public class SignUpResultActivity extends AbstractActivity implements SignUpResultView.Presenter {

    private final SignUpResultView view;

    private final PlaceController placeController;

    @Inject
    public SignUpResultActivity(SignUpResultView view, PlaceController placeController) {
        this.view = view;
        this.placeController = placeController;
        view.setPresenter(this);
    }

    public SignUpResultActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public Map<String, String> getParams() {
        return ((AppPlace) placeController.getWhere()).getArgs();
    }
}
