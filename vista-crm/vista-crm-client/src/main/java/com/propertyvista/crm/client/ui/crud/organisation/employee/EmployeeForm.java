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

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IForm;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.security.UserAuditingConfigurationForm;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeFolder.ParentEmployeeGetter;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;

public class EmployeeForm extends CrmEntityForm<EmployeeDTO> {

    private static final I18n i18n = I18n.get(EmployeeForm.class);

    public EmployeeForm(IForm<EmployeeDTO> view) {
        super(EmployeeDTO.class, view);

        selectTab(addTab(createInfoTab(i18n.tr("Personal Information"))));
        addTab(createPrivilegesTab(i18n.tr("Privileges")));
        addTab(createAuditingConfigurationTab(i18n.tr("Auditing Configuration")));
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().passwordConfirm()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String, ?> component, String value) {
                if (value.equals(get(proto().password()).getValue())) {
                    return null;
                } else {
                    return new ValidationError(component, i18n.tr("The passwords don't match. Please retype the passwords."));
                }
            }
        });

        new PastDateValidation(get(proto().birthDate()));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.employee, get(proto().employeeId()), getValue().getPrimaryKey());
        }

        get(proto().password()).setVisible(isNewEmployee());
        get(proto().passwordConfirm()).setVisible(isNewEmployee());
    }

    public void restrictSecurityRelatedControls(boolean isManager, boolean isSelfEditor) {
        get(proto().enabled()).setVisible(isManager);
        get(proto().requiredPasswordChangeOnNextLogIn()).setVisible(isManager);

        get(proto().roles()).setEditable(!isSelfEditor);

        boolean permitPortfoliosEditing = isManager & !isSelfEditor;

        get(proto().accessAllBuildings()).setViewable(!permitPortfoliosEditing);
        get(proto().portfolios()).setViewable(!permitPortfoliosEditing);
        get(proto().portfolios()).setEditable(permitPortfoliosEditing);

        get(proto().employees()).setViewable(!isManager);
        get(proto().employees()).setEditable(isManager);

        get(proto().userAuditingConfiguration()).setEnabled(isSelfEditor | isManager);
    }

    private FormFlexPanel createInfoTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().employeeId()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().title()), 20).build());

        main.setBR(++row, 0, 1);

        main.setWidget(++row, 0, inject(proto().name(), new NameEditor(i18n.tr("Employee"))));
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

        return main;
    }

    private FormFlexPanel createAuditingConfigurationTab(String title) {
        FormFlexPanel tabContent = new FormFlexPanel(title);

        tabContent.setWidget(0, 0, inject(proto().userAuditingConfiguration(), new UserAuditingConfigurationForm()));

        return tabContent;
    }

    private FormFlexPanel createPrivilegesTab(String title) {

        FormFlexPanel content = new FormFlexPanel(title);

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("Information"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().password()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().passwordConfirm()), 10).build());

        content.setBR(++row, 0, 1);
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().enabled()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().requiredPasswordChangeOnNextLogIn()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().credentialUpdated()), 15).build());

        content.setH1(++row, 0, 2, i18n.tr("Roles"));
        content.setWidget(++row, 0, inject(proto().roles(), new CrmRoleFolder(isEditable())));

        content.setH1(++row, 0, 1, i18n.tr("Portfolios"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().accessAllBuildings()), 5).build());
        content.setWidget(++row, 0, inject(proto().portfolios(), new PortfolioFolder(isEditable())));

        content.setH1(++row, 0, 1, i18n.tr("Subordinates"));
        content.setWidget(++row, 0, inject(proto().employees(), new EmployeeFolder(isEditable(), new ParentEmployeeGetter() {
            @Override
            public Key getParentId() {
                return getValue() != null ? getValue().getPrimaryKey() : null;
            }
        })));

        return content;
    }

    private boolean isNewEmployee() {
        return getValue().id().isNull();
    }

}
