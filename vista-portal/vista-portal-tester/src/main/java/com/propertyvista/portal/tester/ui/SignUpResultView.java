package com.propertyvista.portal.tester.ui;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;

public interface SignUpResultView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {
        Map<String, String> getParams();
    }

}