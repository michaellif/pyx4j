package com.propertyvista.crm.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.IsView;

import com.propertyvista.crm.rpc.dto.communication.CrmCommunicationSystemNotification;
import com.propertyvista.shared.i18n.CompiledLocale;

public interface HeaderView extends IsWidget, IsView {

    public interface HeaderPresenter {

        void navigToLanding();

        void logout();

        boolean isAdminPlace();

        void showUserProfile();

        void showUserPreferences();

        void back2CrmView();

        boolean isSettingsPlace();

        void setLocale(CompiledLocale locale);

        void getSatisfaction();

        void loadMessages();

    }

    void setPresenter(HeaderPresenter presenter);

    void onLogedOut();

    void onLogedIn(String userName);

    void setAvailableLocales(List<CompiledLocale> locales);

    void setCommunicationMessagesCount(CrmCommunicationSystemNotification status);

}