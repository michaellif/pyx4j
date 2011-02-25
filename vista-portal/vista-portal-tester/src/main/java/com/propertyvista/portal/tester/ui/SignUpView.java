package com.propertyvista.portal.tester.ui;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IDebugId;

public interface SignUpView extends IsWidget {

    public static enum Fields implements IDebugId {
        accountType("Account type"),

        userName("User name"),

        password("Password"),

        verifyPassword("Verify password"),

        internalCustomer("Internal customer");

        private final String title;

        Fields(String title) {
            this.title = title;
        }

        @Override
        public String getDebugIdString() {
            return name();
        }

        public String getTitle() {
            return title;
        }

    }

    public void setPresenter(Presenter presenter);

    public interface Presenter {
        void goToSignUpResult(Map<String, String> params);
    }

}