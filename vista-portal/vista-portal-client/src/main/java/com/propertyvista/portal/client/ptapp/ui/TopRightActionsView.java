package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.place.AppPlaceInfo;

public interface TopRightActionsView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {
        AppPlaceInfo[] getActionsPlacesInfo();
    }

}