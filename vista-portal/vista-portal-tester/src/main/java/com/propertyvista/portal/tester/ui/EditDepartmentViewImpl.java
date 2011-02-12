package com.propertyvista.portal.tester.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.portal.tester.domain.Department;
import com.propertyvista.portal.tester.domain.Employee;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolderComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFormComponent;
import com.pyx4j.entity.shared.IList;
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
        public void createLayout() {
            VerticalPanel main = new VerticalPanel();
            setWidget(main);
            main.add(new WidgetDecorator(create(proto().name(), this)));
            main.add(create(proto().manager(), this));
            //main.add(create(proto().employees(), this));
        }

        @Override
        protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
            if (member.getValueClass().equals(Employee.class)) {
                return createEmployeeEditor(member);
            } else {
                return super.createMemberEditor(member);
            }
        }

        @Override
        protected CEntityFolderComponent<?> createMemberFolderEditor(IObject<?> member) {
            if (proto().employees().equals(member)) {
                return createEmployeeListEditor(member);
            } else {
                return super.createMemberFolderEditor(member);
            }
        }

        private CEntityFolderComponent<?> createEmployeeListEditor(IObject<?> member) {
            return new CEntityFolderComponent<Employee>(Employee.class) {
                @Override
                public void createLayout() {
                    VerticalPanel main = new VerticalPanel();
                    setWidget(main);
                    main.add(new HTML("+++++++++++++"));
                }
            };
        }

        private CEntityEditableComponent<?> createEmployeeEditor(IObject<?> member) {
            return new CEntityEditableComponent<Employee>(Employee.class) {
                @Override
                public void createLayout() {
                    VerticalPanel main = new VerticalPanel();
                    setWidget(main);
                    main.add(new WidgetDecorator(create(proto().firstName(), this)));
                }
            };
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
