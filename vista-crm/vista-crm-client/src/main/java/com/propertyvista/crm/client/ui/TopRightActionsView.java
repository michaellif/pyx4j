package com.propertyvista.crm.client.ui;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.crm.client.ui.TopRightActionsViewImpl.Theme;

public interface TopRightActionsView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {

        public PlaceController getPlaceController();

        public void setTheme(Theme theme);

        public void logout();

        public void login();

        public void showAccount();

        public void showAlerts();

        public void showMessages();

        public void showSettings();

        public void back2CrmView();
    }

    public void onLogedOut();

    public void onLogedIn(String userName);

}