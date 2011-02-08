package com.propertyvista.portal.tester.ui;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IDebugId;

public interface SignUpView extends IsWidget {

    public static enum FIELDS implements IDebugId {
        accountType,

        userName,

        password,

        verifyPassword,

        internalCustomer;

        @Override
        public String getDebugIdString() {
            return name();
        }
    }

    public static final String ACCOUNT_TYPE_TITLE = "Account type";

    public static final String USERNAME_TITLE = "User name";

    public static final String PASSWORD_TITLE = "Password";

    public static final String VERIFY_PASSWORD_TITLE = "Verify password";

    public static final String INTERNAL_CUSTOMER_TITLE = "Internal customer";

    public void setPresenter(Presenter presenter);

    public interface Presenter {
        void goToSignUpResult(Map<String, String> params);
    }

}