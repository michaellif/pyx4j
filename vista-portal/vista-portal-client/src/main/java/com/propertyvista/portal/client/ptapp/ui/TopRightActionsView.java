package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.place.AppPlaceListing;

public interface TopRightActionsView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {
        public AppPlaceListing getAppPlaceListing();

        public PlaceController getPlaceController();
    }

}