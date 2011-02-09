package com.propertyvista.portal.tester.ui;

import com.google.gwt.user.client.ui.IsWidget;

public interface EditDepartmentView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public interface Presenter {
        public void save();
    }

}