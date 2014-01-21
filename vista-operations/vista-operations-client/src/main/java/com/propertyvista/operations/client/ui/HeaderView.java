package com.propertyvista.operations.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.AppPlaceContorller;
import com.pyx4j.site.client.IsView;

public interface HeaderView extends IsWidget, IsView {

    public void setPresenter(Presenter presenter);

    public interface Presenter {

        public void navigToLanding();

        public AppPlaceContorller getPlaceController();

        public Place getWhere();

        public void logout();

        public void login();

        public void showAccount();

    }

    public void onLogedOut();

    public void onLogedIn(String userName);
}