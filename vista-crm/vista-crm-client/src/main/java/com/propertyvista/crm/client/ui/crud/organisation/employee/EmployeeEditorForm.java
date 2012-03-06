/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeFolder.ParentEmployeeGetter;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class EmployeeEditorForm extends CrmEntityForm<EmployeeDTO> {

    private static final I18n i18n = I18n.get(EmployeeEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public EmployeeEditorForm() {
        this(false);
    }

    public EmployeeEditorForm(boolean viewMode) {
        super(EmployeeDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        tabPanel.add(createInfoTab(), i18n.tr("Personal Information"));
        tabPanel.add(createPrivilegesTab(), i18n.tr("Privileges"));

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    public void enforceBehaviour() {
        boolean isManager = SecurityController.checkBehavior(VistaCrmBehavior.Organization);

        get(proto().enabled()).setVisible(isManager);
        get(proto().requireChangePasswordOnNextLogIn()).setVisible(isManager);

        get(proto().accessAllBuildings()).setViewable(!isManager);
//        get(proto().roles()).setViewable(!isManager);
//        get(proto().roles()).setEditable(isManager);

        get(proto().portfolios()).setViewable(!isManager);
        get(proto().portfolios()).setEditable(isManager);

        get(proto().employees()).setViewable(!isManager);
        get(proto().employees()).setEditable(isManager);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().password()).setVisible(isNewEmployee());
        get(proto().passwordConfirm()).setVisible(isNewEmployee());

        enforceBehaviour();
    }

    private boolean isNewEmployee() {
        return getValue().id().isNull();
    }

    /**
     * @return <code>true</code> if the current user is editing his own information
     */
    private boolean isSelfEditor() {
        return (getValue() != null) && !isNewEmployee() && !getValue().user().isNull()
                && EqualsHelper.equals(getValue().user().getPrimaryKey(), ClientContext.getUserVisit().getPrincipalPrimaryKey());
    }

    private IsWidget createInfoTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().title()), 20).build());

        main.setBR(++row, 0, 1);

        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().namePrefix()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().firstName()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().middleName()), 10).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().lastName()), 20).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().maidenName()), 20).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name(), new CEntityLabel<Name>()), 25).customLabel(i18n.tr("Employee")).build());
            get(proto().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            get(proto().name()).asWidget().getElement().getStyle().setFontSize(1.1, Unit.EM);
        }

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sex()), 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().birthDate()), 9).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().homePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().workPhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email()), 25).build());
        get(proto().email()).setMandatory(true);

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 50).build());

        return new CrmScrollPanel(main);
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    private IsWidget createPrivilegesTab() {

        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Information"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().password()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().passwordConfirm()), 10).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().enabled()), 5).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().requireChangePasswordOnNextLogIn()), 5).build());

        main.setH1(++row, 0, 2, i18n.tr("Roles"));
        main.setWidget(++row, 0, inject(proto().roles(), new CrmRoleFolder(isEditable())));

        main.setH1(++row, 0, 1, i18n.tr("Portfolios"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().accessAllBuildings()), 5).build());
        main.setWidget(++row, 0, inject(proto().portfolios(), new PortfolioFolder(isEditable())));

        main.setH1(++row, 0, 1, i18n.tr("Subordinates"));
        main.setWidget(++row, 0, inject(proto().employees(), new EmployeeFolder(isEditable(), new ParentEmployeeGetter() {
            @Override
            public Key getParentId() {
                return getValue() != null ? getValue().getPrimaryKey() : null;
            }
        })));

        return new CrmScrollPanel(main);
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().passwordConfirm()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationFailure isValid(CComponent<String, ?> component, String value) {
                if (value.equals(get(proto().password()).getValue())) {
                    return null;
                } else {
                    return new ValidationFailure(i18n.tr("The passwords don't match. Please retype the passwords."));
                }
            }
        });

        new PastDateValidation(get(proto().birthDate()));
    }

}
