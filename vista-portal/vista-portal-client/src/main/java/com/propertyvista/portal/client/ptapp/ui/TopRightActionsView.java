package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.client.ptapp.ui.TopRightActionsViewImpl.Theme;

import com.pyx4j.site.client.place.AppPlaceListing;

public interface TopRightActionsView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {

        public AppPlaceListing getAppPlaceListing();

        public PlaceController getPlaceController();

        public void setTheme(Theme theme);

        public void logout();

        public void login();

    }

    public void onLogedOut();

    public void onLogedIn(String userName);

}