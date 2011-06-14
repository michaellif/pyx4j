package com.propertyvista.admin.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.admin.client.ui.TopRightActionsViewImpl.Theme;

public interface TopRightActionsView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {

        public PlaceController getPlaceController();

        public Place getWhere();

        public void setTheme(Theme theme);

        public void logout();

        public void login();

        public void showAccount();

        public void showAlerts();

        public void showMessages();

        public void showSettings();
    }

    public void onLogedOut();

    public void onLogedIn(String userName);
}