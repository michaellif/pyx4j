package com.propertyvista.portal.tester.ui;

import com.google.gwt.user.client.ui.IsWidget;

public interface TopRightActionsView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {
        void goToSignUp();
    }

}