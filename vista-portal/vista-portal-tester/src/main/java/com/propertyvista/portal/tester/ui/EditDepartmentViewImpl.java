package com.propertyvista.portal.tester.ui;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.propertyvista.portal.tester.domain.Department;
import com.propertyvista.portal.tester.domain.Employee;
import com.propertyvista.portal.tester.resources.SiteImages;

import com.pyx4j.entity.client.ui.flex.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.flex.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.decorators.BasicWidgetDecorator;

public class EditDepartmentViewImpl extends FlowPanel implements EditDepartmentView {

    private static I18n i18n = I18nFactory.getI18n(EditDepartmentViewImpl.class);

    private Presenter presenter;

    private final CEntityForm<Department> form;

    private static class DepartmentForm extends CEntityForm<Department> {

        public DepartmentForm() {
            super(Department.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel main = new FlowPanel();
            main.add(new BasicWidgetDecorator(create(proto().name(), this), 140, 180));
            main.add(new Label("Manager:"));
            main.add(create(proto().manager(), this));
            main.add(new Label("Employees:"));
            main.add(create(proto().employees(), this));
            main.add(new Label("Contractors:"));
            main.add(create(proto().contractors(), this));
            return main;
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
                return createEmployeeFolderEditorColumns();
            } else if (member.equals(proto().contractors())) {
                return createEmployeeFolderEditorForms();
            } else {
                return super.createMemberFolderEditor(member);
            }
        }

        private CEntityEditableComponent<Employee> createEmployeeEditor() {
            return new CEntityEditableComponent<Employee>(Employee.class) {
                @Override
                public IsWidget createContent() {
                    FlowPanel main = new FlowPanel();
                    main.add(new BasicWidgetDecorator(create(proto().firstName(), this), 140, 180));
                    main.add(new BasicWidgetDecorator(create(proto().lastName(), this), 140, 180));
                    main.add(new BasicWidgetDecorator(create(proto().phone(), this)));
                    return main;
                }
            };
        }

        private CEntityFolder<Employee> createEmployeeFolderEditorColumns() {
            return new CEntityFolder<Employee>() {

                private List<EntityFolderColumnDescriptor> columns;

                {
                    Employee proto = EntityFactory.getEntityPrototype(Employee.class);
                    columns = new ArrayList<EntityFolderColumnDescriptor>();
                    columns.add(new EntityFolderColumnDescriptor(proto.firstName(), "120px"));
                    columns.add(new EntityFolderColumnDescriptor(proto.lastName(), "120px"));
                    columns.add(new EntityFolderColumnDescriptor(proto.phone(), "100px"));
                    columns.add(new EntityFolderColumnDescriptor(proto.reliable(), "20px"));
                }

                @Override
                protected FolderDecorator<Employee> createFolderDecorator() {
                    return new TableFolderDecorator<Employee>(columns, SiteImages.INSTANCE.addRow());
                }

                @Override
                protected CEntityFolderItem<Employee> createItem() {
                    return createEmployeeRowEditor(columns);
                }

                private CEntityFolderItem<Employee> createEmployeeRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                    return new CEntityFolderRow<Employee>(Employee.class, columns, DepartmentForm.this) {

                        @Override
                        public FolderItemDecorator createFolderItemDecorator() {
                            return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow());
                        }

                    };
                }

            };

        }

        private CEntityFolder<Employee> createEmployeeFolderEditorForms() {
            return new CEntityFolder<Employee>() {

                @Override
                protected FolderDecorator<Employee> createFolderDecorator() {
                    return new BoxFolderDecorator<Employee>(SiteImages.INSTANCE.addRow());

                }

                @Override
                protected CEntityFolderItem<Employee> createItem() {

                    return new CEntityFolderItem<Employee>(Employee.class) {

                        @Override
                        public IsWidget createContent() {
                            FlowPanel main = new FlowPanel();
                            main.add(new BasicWidgetDecorator(create(proto().firstName(), this), 140, 180));
                            main.add(new BasicWidgetDecorator(create(proto().lastName(), this), 140, 180));
                            main.add(new BasicWidgetDecorator(create(proto().phone(), this)));
                            main.add(new BasicWidgetDecorator(create(proto().reliable(), this)));
                            return main;
                        }

                        @Override
                        public FolderItemDecorator createFolderItemDecorator() {
                            return new BoxFolderItemDecorator(SiteImages.INSTANCE.removeRow());
                        }
                    };
                }

            };
        }
    }

    public EditDepartmentViewImpl() {
        Label labael = new Label("DepartmentView");
        labael.setSize("300px", "100px");
        add(labael);

        form = new DepartmentForm();
        form.initialize();

        add(form);

        Button signUpButton = new Button(i18n.tr("Save"));
        signUpButton.ensureDebugId(CrudDebugId.Crud_Save.toString());
        signUpButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.save(form.getValue());
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
