package com.propertyvista.operations.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;

public interface HeaderView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {

        public void navigToLanding();

        public PlaceController getPlaceController();

        public Place getWhere();

        public void logout();

        public void login();

        public void showAccount();

    }

    public void onLogedOut();

    public void onLogedIn(String userName);
}