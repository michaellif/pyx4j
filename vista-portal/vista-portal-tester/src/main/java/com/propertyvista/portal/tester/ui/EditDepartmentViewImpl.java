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

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.decorators.BasicWidgetDecorator;

import com.propertyvista.portal.tester.domain.Department;
import com.propertyvista.portal.tester.domain.Employee;
import com.propertyvista.portal.tester.resources.SiteImages;

public class EditDepartmentViewImpl extends FlowPanel implements EditDepartmentView {

    private static I18n i18n = I18nFactory.getI18n(EditDepartmentViewImpl.class);

    private Presenter presenter;

    private final CEntityEditor<Department> form;

    private static class DepartmentForm extends CEntityEditor<Department> {

        public DepartmentForm() {
            super(Department.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel main = new FlowPanel();
            main.add(new BasicWidgetDecorator(inject(proto().name()), 140, 180));
            main.add(new Label("Manager:"));
            main.add(inject(proto().manager()));
            main.add(new Label("Employees:"));
            main.add(inject(proto().employees()));
            main.add(new Label("Contractors:"));
            main.add(inject(proto().contractors()));
            return main;
        }

        @Override
        public CEditableComponent<?, ?> create(IObject<?> member) {
            if (member.getValueClass().equals(Employee.class)) {
                return createEmployeeEditor();
            } else if (member == proto().employees()) {
                return createEmployeeFolderEditorColumns();
            } else if (member == proto().contractors()) {
                return createEmployeeFolderEditorForms();
            } else {
                return super.create(member);
            }
        }

        private CEntityEditor<Employee> createEmployeeEditor() {
            return new CEntityEditor<Employee>(Employee.class) {
                @Override
                public IsWidget createContent() {
                    FlowPanel main = new FlowPanel();
                    main.add(new BasicWidgetDecorator(inject(proto().firstName()), 140, 180));
                    main.add(new BasicWidgetDecorator(inject(proto().lastName()), 140, 180));
                    main.add(new BasicWidgetDecorator(inject(proto().phone())));
                    return main;
                }
            };
        }

        private CEntityFolder<Employee> createEmployeeFolderEditorColumns() {
            return new CEntityFolder<Employee>(Employee.class) {

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
                protected IFolderDecorator<Employee> createFolderDecorator() {
                    return new TableFolderDecorator<Employee>(columns, SiteImages.INSTANCE.addRow());
                }

                @Override
                protected CEntityFolderItemEditor<Employee> createItem() {
                    return createEmployeeRowEditor(columns);
                }

                private CEntityFolderItemEditor<Employee> createEmployeeRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                    return new CEntityFolderRowEditor<Employee>(Employee.class, columns) {

                        @Override
                        public IFolderItemDecorator createFolderItemDecorator() {
                            return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow());
                        }

                    };
                }

            };

        }

        private CEntityFolder<Employee> createEmployeeFolderEditorForms() {
            return new CEntityFolder<Employee>(Employee.class) {

                @Override
                protected IFolderDecorator<Employee> createFolderDecorator() {
                    return new BoxFolderDecorator<Employee>(SiteImages.INSTANCE.addRow());

                }

                @Override
                protected CEntityFolderItemEditor<Employee> createItem() {

                    return new CEntityFolderItemEditor<Employee>(Employee.class) {

                        @Override
                        public IsWidget createContent() {
                            FlowPanel main = new FlowPanel();
                            main.add(new BasicWidgetDecorator(inject(proto().firstName()), 140, 180));
                            main.add(new BasicWidgetDecorator(inject(proto().lastName()), 140, 180));
                            main.add(new BasicWidgetDecorator(inject(proto().phone())));
                            main.add(new BasicWidgetDecorator(inject(proto().reliable())));
                            return main;
                        }

                        @Override
                        public IFolderItemDecorator createFolderItemDecorator() {
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
