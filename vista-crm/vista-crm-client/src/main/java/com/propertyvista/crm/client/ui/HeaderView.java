package com.propertyvista.crm.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.IsView;

import com.propertyvista.shared.i18n.CompiledLocale;

public interface HeaderView extends IsWidget, IsView {

    void setPresenter(Presenter presenter);

    interface Presenter {

        void navigToLanding();

        void logout();

        void login();

        void showHome();

        void showAccount();

        void showProperties();

        void showMessages(int x, int y);

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

    void setDisplayThisIsDemoWarning(boolean displayThisIsDemoWarning);

    void setNumberOfMessages(int messagesNum);
}