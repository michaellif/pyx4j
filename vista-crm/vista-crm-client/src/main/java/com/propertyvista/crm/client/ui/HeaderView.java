package com.propertyvista.crm.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.IsView;

import com.propertyvista.shared.i18n.CompiledLocale;

public interface HeaderView extends IsWidget, IsView {

    public interface HeaderPresenter {

        void navigToLanding();

        void logout();

        void login();

        boolean isAdminPlace();

        void showAccount();

        void showProperties();

        void showMessages(int x, int y);

        void back2CrmView();

        boolean isSettingsPlace();

        void setLocale(CompiledLocale locale);

        void getSatisfaction();

    }

    void setPresenter(HeaderPresenter presenter);

    void onLogedOut();

    void onLogedIn(String userName);

    void setAvailableLocales(List<CompiledLocale> locales);

    void setDisplayThisIsProductionWarning(boolean displayThisIsProductionWarning);

    void setDisplayThisIsDemoWarning(boolean displayThisIsDemoWarning);

    void setNumberOfMessages(int messagesNum);
}