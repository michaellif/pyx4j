package com.propertyvista.crm.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.shared.CompiledLocale;

public interface TopRightActionsView extends IsWidget {

    void setPresenter(Presenter presenter);

    interface Presenter {

        void logout();

        void login();

        void showAccount();

        void showAlerts();

        void showMessages();

        void showSettings();

        void back2CrmView();

        void SwitchCrmAndSettings();

        boolean isSettingsPlace();

        void setLocale(CompiledLocale locale);

        void getSatisfaction();
    }

    void onLogedOut();

    void onLogedIn(String userName);

    void setAvailableLocales(List<CompiledLocale> locales);

    void setDisplayThisIsProductionWarning(boolean displayThisIsProductionWarning);
}