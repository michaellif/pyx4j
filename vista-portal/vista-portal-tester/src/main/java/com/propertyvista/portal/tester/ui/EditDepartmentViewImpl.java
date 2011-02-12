package com.propertyvista.portal.tester.ui;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.portal.tester.domain.Department;
import com.propertyvista.portal.tester.domain.Employee;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolderComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFormComponent;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;

public class EditDepartmentViewImpl extends VerticalPanel implements EditDepartmentView {

    private static I18n i18n = I18nFactory.getI18n(EditDepartmentViewImpl.class);

    private Presenter presenter;

    private final CEntityEditableComponent<Department> editor;

    private static class DepartmentComponent extends CEntityFormComponent<Department> {

        public DepartmentComponent() {
            super(Department.class);
        }

        @Override
        public void createContent() {
            VerticalPanel main = new VerticalPanel();
            main.add(new WidgetDecorator(create(proto().name(), this)));
            main.add(create(proto().manager(), this));
            main.add(create(proto().employees(), this));
            setWidget(main);
        }

        @Override
        protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
            if (member.getValueClass().equals(Employee.class)) {
                return createEmployeeEditor();
            } else {
                return super.createMemberEditor(member);
            }
        }

        @Override
        protected CEntityFolderComponent<?> createMemberFolderEditor(IObject<?> member) {
            if (member.equals(proto().employees())) {
                return createEmployeeFolderEditor();
            } else {
                return super.createMemberFolderEditor(member);
            }
        }

        private CEntityEditableComponent<Employee> createEmployeeEditor() {
            return new CEntityEditableComponent<Employee>(Employee.class) {
                @Override
                public void createContent() {
                    VerticalPanel main = new VerticalPanel();
                    main.add(new WidgetDecorator(create(proto().firstName(), this)));
                    main.add(new WidgetDecorator(create(proto().lastName(), this)));
                    main.add(new WidgetDecorator(create(proto().phone(), this)));
                    setWidget(main);
                }
            };
        }

        private CEntityFolderComponent<Employee> createEmployeeFolderEditor() {
            return new CEntityFolderComponent<Employee>() {

                @Override
                public void createContent() {
                    setFolderDecorator(new EmployeeFolder());
                }

                @Override
                protected CEntityEditableComponent<Employee> createItem() {
                    return createEmployeeRowEditor();
                }

            };
        }

        private CEntityEditableComponent<Employee> createEmployeeRowEditor() {
            return new CEntityEditableComponent<Employee>(Employee.class) {
                @Override
                public void createContent() {
                    FlowPanel main = new FlowPanel();
                    main.setWidth("100%");
                    for (EntityFolderColumnDescriptor column : EmployeeFolder.columns) {
                        CComponent<?> component = create(column.getObject(), this);
                        component.setWidth(column.getWidth());
                        component.asWidget().getElement().getStyle().setProperty("cssFloat", "left");
                        main.add(component);
                    }
                    setWidget(main);
                }

            };
        }

        static class EmployeeFolder extends VerticalPanel implements FolderDecorator {

            public static List<EntityFolderColumnDescriptor> columns;

            static Employee proto = EntityFactory.getEntityPrototype(Employee.class);

            static {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.firstName(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto.lastName(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto.phone(), "100px"));
            }

            private final SimplePanel content;

            EmployeeFolder() {

                FlowPanel header = new FlowPanel();
                header.setWidth("100%");
                for (EntityFolderColumnDescriptor column : columns) {
                    Label label = new Label(column.getObject().getMeta().getCaption());
                    label.setWidth(column.getWidth());
                    label.asWidget().getElement().getStyle().setProperty("cssFloat", "left");
                    label.asWidget().getElement().getStyle().setFloat(Float.LEFT);
                    header.add(label);
                }

                add(header);

                content = new SimplePanel();
                add(content);

            }

            @Override
            public void setWidget(IsWidget w) {
                content.setWidget(w);
            }

        }
    }

    public EditDepartmentViewImpl() {
        Label labael = new Label("DepartmentView");
        labael.setSize("300px", "100px");
        add(labael);

        editor = new DepartmentComponent();

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
        editor.populate(entity);
    }
}
