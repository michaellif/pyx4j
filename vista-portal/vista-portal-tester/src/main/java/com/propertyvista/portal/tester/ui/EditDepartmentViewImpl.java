package com.propertyvista.portal.tester.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.portal.tester.domain.Department;

import com.pyx4j.entity.client.ui.BaseEditableComponentFactory;
import com.pyx4j.entity.client.ui.EntityPresenter;
import com.pyx4j.essentials.client.crud.CrudDebugId;

public class EditDepartmentViewImpl extends VerticalPanel implements EditDepartmentView {

    private static I18n i18n = I18nFactory.getI18n(EditDepartmentViewImpl.class);

    private Presenter presenter;

    private final EntityPresenter<Department> ep;

    public EditDepartmentViewImpl() {
        Label labael = new Label("DepartmentView");
        labael.setSize("300px", "100px");
        add(labael);

        ep = EntityPresenter.create(new BaseEditableComponentFactory(), Department.class);

        add(ep.create(ep.proto().name()));

        Button signUpButton = new Button(i18n.tr("Save"));
        signUpButton.ensureDebugId(CrudDebugId.Crud_Save.toString());
        signUpButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.save(ep.getValue());
            }

        });
        signUpButton.getElement().getStyle().setProperty("margin", "3px 20px 3px 8px");
        add(signUpButton);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(Department entity) {
        ep.populate(entity);
    }
}
