package com.propertyvista.crm.client.ui;

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.crm.client.ui.TopRightActionsViewImpl.Theme;
import com.propertyvista.shared.CompiledLocale;

public interface TopRightActionsView extends IsWidget {

    void setPresenter(Presenter presenter);

    interface Presenter {

        PlaceController getPlaceController();

        Place getWhere();

        void setTheme(Theme theme);

        void logout();

        void login();

        void showAccount();

        void showAlerts();

        void showMessages();

        void showSettings();

        void back2CrmView();

        void SwitchCrmAndSettings();

        void setLocale(CompiledLocale locale);
    }

    void onLogedOut();

    void onLogedIn(String userName);

    void setAvailableLocales(List<CompiledLocale> locales);
}