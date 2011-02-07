package com.propertyvista.portal.tester.ui;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;

public interface SignUpView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {
        void goToSignUpResult(Map<String, String> params);
    }

}