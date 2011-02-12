package com.propertyvista.portal.tester.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.portal.tester.domain.Department;
import com.propertyvista.portal.tester.domain.Employee;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolderComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFormComponent;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.client.crud.CrudDebugId;
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
            return new CEntityFolderComponent<Employee>() {

                @Override
                public void createContent() {
                    setFolderDecorator(new DepartmentFolder());
                }

                @Override
                protected CEntityEditableComponent<Employee> createItem() {
                    return createEmployeeEditor();
                }

            };
        }

        private CEntityEditableComponent<Employee> createEmployeeEditor() {
            return new CEntityEditableComponent<Employee>(Employee.class) {
                @Override
                public void createContent() {
                    VerticalPanel main = new VerticalPanel();
                    main.add(new WidgetDecorator(create(proto().firstName(), this)));
                    setWidget(main);
                }
            };
        }

        class DepartmentFolder extends VerticalPanel implements FolderDecorator {

            private final SimplePanel content;

            DepartmentFolder() {
                content = new SimplePanel();
                add(new HTML("+++++++++++++"));
                add(content);
                add(new HTML("+++++++++++++"));

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
