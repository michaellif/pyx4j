package com.propertyvista.portal.tester.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.tester.domain.Department;
import com.propertyvista.portal.tester.domain.Employee;

public class EditDepartmentViewImpl extends FlowPanel implements EditDepartmentView {

    private static I18n i18n = I18n.get(EditDepartmentViewImpl.class);

    private Presenter presenter;

    private final CEntityEditor<Department> form;

    private static class DepartmentForm extends CEntityEditor<Department> {

        public DepartmentForm() {
            super(Department.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel main = new FlowPanel();
            main.add(new Label("Manager:"));
            main.add(inject(proto().manager()));
            main.add(new Label("Employees:"));
            main.add(inject(proto().employees()));
            main.add(new Label("Contractors:"));
            main.add(inject(proto().contractors()));
            return main;
        }

        private CEntityEditor<Employee> createEmployeeEditor() {
            return new CEntityEditor<Employee>(Employee.class) {
                @Override
                public IsWidget createContent() {
                    FlowPanel main = new FlowPanel();
                    main.add(new WidgetDecorator(inject(proto().phone())));
                    return main;
                }
            };
        }

    }

    public EditDepartmentViewImpl() {
        Label labael = new Label("DepartmentView");
        labael.setSize("300px", "100px");
        add(labael);

        form = new DepartmentForm();
        form.initContent();

        add(form);

        Button signUpButton = new Button(i18n.tr("Save"));
        signUpButton.ensureDebugId(CrudDebugId.Crud_Save.toString());
        signUpButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                // presenter.save(form.getValue());
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
        form.populate(entity);
    }
}
