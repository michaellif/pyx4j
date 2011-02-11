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
import com.pyx4j.entity.client.ui.flex.FlexEditableComponentFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;

public class EditDepartmentViewImpl extends VerticalPanel implements EditDepartmentView {

    private static I18n i18n = I18nFactory.getI18n(EditDepartmentViewImpl.class);

    private Presenter presenter;

    private final DepartmentComponentFactory factory;

    private static class DepartmentComponentFactory extends FlexEditableComponentFactory<Department> {

        public DepartmentComponentFactory() {
            super(Department.class);
        }

        @Override
        public void createEntityLayout() {
            VerticalPanel main = new VerticalPanel();
            setWidget(main);
            main.add(new WidgetDecorator(create(proto().name())));
            main.add(create(proto().manager()));
            //content().add(create(proto().employees()));
        }

        @Override
        protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
            if (member.getValueClass().equals(Employee.class)) {
                return createEmployeeEditor(member);
            } else {
                return super.createMemberEditor(member);
            }
        }

        public CEntityEditableComponent<?> createEmployeeEditor(IObject<?> member) {
            return new CEntityEditableComponent<Employee>(Employee.class, this) {

                @Override
                public void createLayout() {
                    VerticalPanel main = new VerticalPanel();
                    setWidget(main);
                    main.add(new WidgetDecorator(create(proto().firstName())));
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

        factory = new DepartmentComponentFactory();

        final CEntityEditableComponent<Department> editor = factory.getEntityEditor();

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
        factory.populate(entity);
    }
}
