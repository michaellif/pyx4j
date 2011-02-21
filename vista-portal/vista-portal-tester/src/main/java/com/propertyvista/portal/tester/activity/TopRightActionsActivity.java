package com.propertyvista.portal.tester.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.tester.SiteMap;
import com.propertyvista.portal.tester.ui.TopRightActionsView;

import com.pyx4j.site.rpc.AppPlace;

public class TopRightActionsActivity extends AbstractActivity implements TopRightActionsView.Presenter {

    private final TopRightActionsView view;

    private final PlaceController placeController;

    @Inject
    public TopRightActionsActivity(TopRightActionsView view, PlaceController placeController) {
        this.view = view;
        this.placeController = placeController;
        view.setPresenter(this);
    }

    public TopRightActionsActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void goToSignUp() {
        placeController.goTo(new SiteMap.SignUp());
    }
}
