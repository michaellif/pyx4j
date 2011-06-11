package com.propertyvista.portal.tester.ui;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.portal.tester.domain.Department;

public interface EditDepartmentView extends IsWidget {

    public void setPresenter(Presenter presenter);

    public void populate(Department entity);

    public interface Presenter {
        public void save(Department entity);
    }

}