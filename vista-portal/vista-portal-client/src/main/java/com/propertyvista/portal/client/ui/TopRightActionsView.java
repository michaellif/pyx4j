package com.propertyvista.portal.client.ui;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;

public interface TopRightActionsView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {

        public PlaceController getPlaceController();

    }

}