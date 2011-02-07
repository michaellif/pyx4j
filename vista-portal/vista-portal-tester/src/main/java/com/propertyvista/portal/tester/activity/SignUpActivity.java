package com.propertyvista.portal.tester.activity;

import java.util.Map;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.tester.SiteMap;
import com.propertyvista.portal.tester.SiteMap.SignUpResult;
import com.propertyvista.portal.tester.ui.SignUpView;

import com.pyx4j.site.client.place.AppPlace;

public class SignUpActivity extends AbstractActivity implements SignUpView.Presenter {

    private final SignUpView view;

    private final PlaceController placeController;

    @Inject
    public SignUpActivity(SignUpView view, PlaceController placeController) {
        this.view = view;
        this.placeController = placeController;
        view.setPresenter(this);
    }

    public SignUpActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void goToSignUpResult(Map<String, String> params) {
        SignUpResult place = new SiteMap.SignUpResult();
        place.setArgs(params);
        placeController.goTo(place);
    }

}
