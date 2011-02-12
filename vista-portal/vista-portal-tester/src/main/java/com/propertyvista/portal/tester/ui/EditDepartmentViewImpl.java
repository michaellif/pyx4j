package com.propertyvista.portal.tester.ui;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.propertyvista.portal.tester.domain.Department;
import com.propertyvista.portal.tester.domain.Employee;
import com.propertyvista.portal.tester.resources.SiteImages;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;

public class EditDepartmentViewImpl extends FlowPanel implements EditDepartmentView {

    private static I18n i18n = I18nFactory.getI18n(EditDepartmentViewImpl.class);

    private Presenter presenter;

    private final CEntityEditableComponent<Department> editor;

    private static class DepartmentForm extends CEntityForm<Department> {

        public DepartmentForm() {
            super(Department.class);
        }

        @Override
        public void createContent() {
            FlowPanel main = new FlowPanel();
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
        protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
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
                    FlowPanel main = new FlowPanel();
                    main.add(new WidgetDecorator(create(proto().firstName(), this)));
                    main.add(new WidgetDecorator(create(proto().lastName(), this)));
                    main.add(new WidgetDecorator(create(proto().phone(), this)));
                    setWidget(main);
                }
            };
        }

        private CEntityFolder<Employee> createEmployeeFolderEditor() {
            return new CEntityFolder<Employee>() {

                private List<EntityFolderColumnDescriptor> columns;

                {
                    Employee proto = EntityFactory.getEntityPrototype(Employee.class);
                    columns = new ArrayList<EntityFolderColumnDescriptor>();
                    columns.add(new EntityFolderColumnDescriptor(proto.firstName(), "120px"));
                    columns.add(new EntityFolderColumnDescriptor(proto.lastName(), "120px"));
                    columns.add(new EntityFolderColumnDescriptor(proto.phone(), "100px"));
                }

                @Override
                public void createContent() {
                    setFolderDecorator(new TableFolderDecorator(columns, SiteImages.INSTANCE.addRow()));
                }

                @Override
                protected CEntityEditableComponent<Employee> createItem() {
                    return createEmployeeRowEditor(columns);
                }

                private CEntityEditableComponent<Employee> createEmployeeRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                    return new CEntityFolderRow<Employee>(Employee.class, columns, DepartmentForm.this, SiteImages.INSTANCE.removeRow());
                }
            };

        }

    }

    public EditDepartmentViewImpl() {
        Label labael = new Label("DepartmentView");
        labael.setSize("300px", "100px");
        add(labael);

        editor = new DepartmentForm();

        add(editor);

        Button signUpButton = new Button(i18n.tr("Save"));
        signUpButton.ensureDebugId(CrudDebugId.Crud_Save.toString());
        signUpButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.save(editor.getValue());
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
