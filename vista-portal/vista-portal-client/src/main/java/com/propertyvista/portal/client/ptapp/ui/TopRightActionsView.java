package com.propertyvista.portal.client.ptapp.ui;

import java.util.Collection;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.client.ptapp.activity.TopRightActionsActivity.Theme;

import com.pyx4j.site.client.place.AppPlaceListing;

public interface TopRightActionsView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {
        public AppPlaceListing getAppPlaceListing();

        public PlaceController getPlaceController();

        public Collection<Theme> getThemes();

        public Theme getCurrentTheme();

        public void setTheme(Theme theme);

        public void logout();

    }

}