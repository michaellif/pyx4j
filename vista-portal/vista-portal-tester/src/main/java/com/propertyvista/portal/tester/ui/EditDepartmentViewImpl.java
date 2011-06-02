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

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.BoxFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.BoxFolderItemEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderItemEditorDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CEditableComponent;
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

        private CEntityFolderEditor<Employee> createEmployeeFolderEditorColumns() {
            return new CEntityFolderEditor<Employee>(Employee.class) {

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
                protected IFolderEditorDecorator<Employee> createFolderDecorator() {
                    return new TableFolderEditorDecorator<Employee>(columns, SiteImages.INSTANCE.addRow());
                }

                @Override
                protected CEntityFolderItemEditor<Employee> createItem() {
                    return createEmployeeRowEditor(columns);
                }

                private CEntityFolderItemEditor<Employee> createEmployeeRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                    return new CEntityFolderRowEditor<Employee>(Employee.class, columns) {

                        @Override
                        public IFolderItemEditorDecorator createFolderItemDecorator() {
                            return new TableFolderItemEditorDecorator(SiteImages.INSTANCE.removeRow());
                        }

                    };
                }

            };

        }

        private CEntityFolderEditor<Employee> createEmployeeFolderEditorForms() {
            return new CEntityFolderEditor<Employee>(Employee.class) {

                @Override
                protected IFolderEditorDecorator<Employee> createFolderDecorator() {
                    return new BoxFolderEditorDecorator<Employee>(SiteImages.INSTANCE.addRow());

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
                        public IFolderItemEditorDecorator createFolderItemDecorator() {
                            return new BoxFolderItemEditorDecorator(SiteImages.INSTANCE.removeRow());
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
