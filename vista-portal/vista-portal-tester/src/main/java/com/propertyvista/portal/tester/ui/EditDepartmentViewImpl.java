package com.propertyvista.portal.tester.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.portal.tester.domain.Department;
import com.propertyvista.portal.tester.domain.Employee;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.EntityChangeManager;
import com.pyx4j.entity.client.ui.flex.FlexEditableComponentFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;

public class EditDepartmentViewImpl extends VerticalPanel implements EditDepartmentView {

    private static I18n i18n = I18nFactory.getI18n(EditDepartmentViewImpl.class);

    private Presenter presenter;

    private final EntityChangeManager<Department> changeManager;

    private final CEntityEditableComponent<Department, VerticalPanel> editor;

    private static class EmployeeEditableComponentFactory extends FlexEditableComponentFactory {

        @Override
        protected CEditableComponent<?, ?> createEntityEditor(IObject<?> member) {
            if (member.getValueClass().equals(Employee.class)) {
                return createEmployeeEditor(member);
            } else {
                return super.createEntityEditor(member);
            }
        }

        public CEditableComponent<?, ?> createEmployeeEditor(IObject<?> member) {
            return new CEntityEditableComponent<Employee, VerticalPanel>(Employee.class, new VerticalPanel(), this) {

                @Override
                public void createLayout() {
                    content().add(new WidgetDecorator(create(proto().firstName())));
                }

            };
        }
    }

    public EditDepartmentViewImpl() {
        Label labael = new Label("DepartmentView");
        labael.setSize("300px", "100px");
        add(labael);

        //editor = CEntityEditableComponent.create(Department.class, new VerticalPanel(), new EmployeeEditableComponentFactory());
        //editor.content().add(editor.binder().create(editor.proto().name()));
        //editor.content().add(editor.binder().create(editor.proto().employees()));

        editor = new CEntityEditableComponent<Department, VerticalPanel>(Department.class, new VerticalPanel(), new EmployeeEditableComponentFactory()) {

            @Override
            public void createLayout() {
                content().add(new WidgetDecorator(create(proto().name())));
                content().add(create(proto().manager()));
                //content().add(create(proto().employees()));
            }

        };

        changeManager = new EntityChangeManager<Department>(Department.class);

        add(editor);

        Button signUpButton = new Button(i18n.tr("Save"));
        signUpButton.ensureDebugId(CrudDebugId.Crud_Save.toString());
        signUpButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.save(editor.binder().getValue());
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
        changeManager.populate(entity);
        editor.populate(changeManager.getValue());
    }
}
