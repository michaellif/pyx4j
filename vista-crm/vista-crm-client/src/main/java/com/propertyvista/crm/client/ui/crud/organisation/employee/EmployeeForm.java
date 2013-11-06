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

import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.gwt.shared.IFileURLBuilder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.security.UserAuditingConfigurationForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.organisation.common.BuildingFolder;
import com.propertyvista.crm.client.ui.crud.organisation.common.PortfolioFolder;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeFolder.ParentEmployeeGetter;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.organization.EmployeeSignatureUploadService;
import com.propertyvista.domain.company.EmployeeSignature;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.company.Notification.NotificationType;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;

public class EmployeeForm extends CrmEntityForm<EmployeeDTO> {

    private static final I18n i18n = I18n.get(EmployeeForm.class);

    private final TwoColumnFlexFormPanel buildingsAccessPanel = new TwoColumnFlexFormPanel();

    public EmployeeForm(IForm<EmployeeDTO> view) {
        super(EmployeeDTO.class, view);

        selectTab(addTab(createInfoTab(i18n.tr("Personal Information"))));
        addTab(createPrivilegesTab(i18n.tr("Privileges")));
        addTab(createAuditingConfigurationTab(i18n.tr("Auditing")));
        addTab(createAlertsTab(i18n.tr("Alerts")));
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().passwordConfirm()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String> component, String value) {
                if (value.equals(get(proto().password()).getValue())) {
                    return null;
                } else {
                    return new ValidationError(component, i18n.tr("The passwords don't match. Please retype the passwords."));
                }
            }
        });

        get(proto().birthDate()).addValueValidator(new BirthdayDateValidator());
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.employee, get(proto().employeeId()), getValue().getPrimaryKey());
        }

        get(proto().password()).setVisible(isNewEmployee());
        get(proto().passwordConfirm()).setVisible(isNewEmployee());

        buildingsAccessPanel.setVisible(getValue().restrictAccessToSelectedBuildingsAndPortfolios().getValue(false));
    }

    public void restrictSecurityRelatedControls(boolean isManager, boolean isSelfEditor) {
        get(proto().isSecurityQuestionSet()).setEditable(false);
        get(proto().enabled()).setVisible(isManager);
        get(proto().requiredPasswordChangeOnNextLogIn()).setVisible(isManager);

        get(proto().roles()).setEditable(!isSelfEditor);

        boolean permitPortfoliosEditing = (isManager && !isSelfEditor);

        get(proto().restrictAccessToSelectedBuildingsAndPortfolios()).setEditable(permitPortfoliosEditing);
        get(proto().buildingAccess()).setEditable(permitPortfoliosEditing);
        get(proto().buildingAccess()).setEditable(permitPortfoliosEditing);

        get(proto().portfolios()).setEditable(permitPortfoliosEditing);
        get(proto().portfolios()).setEditable(permitPortfoliosEditing);

        get(proto().employees()).setEditable(isManager);
        get(proto().employees()).setEditable(isManager);

        get(proto().userAuditingConfiguration()).setEnabled(isSelfEditor || isManager);
    }

    private TwoColumnFlexFormPanel createInfoTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int row = -1;
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().employeeId()), 10).build());
        main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().title()), 20).build());

        main.setWidget(++row, 0, 2, inject(proto().name(), new NameEditor(i18n.tr("Employee"))));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().sex()), 7).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().birthDate()), 9).build());

        main.setBR(++row, 0, 2);
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().homePhone()), 15).build());
        main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().workPhone()), 15).build());
        main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().email()), 22).build());
        get(proto().email()).setMandatory(true);

        main.setBR(++row, 0, 2);
        main.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().description()), true).build());

        main.setH3(++row, 0, 2, i18n.tr("Signature"));

        CImage<EmployeeSignature> signature = new CImage<EmployeeSignature>(GWT.<EmployeeSignatureUploadService> create(EmployeeSignatureUploadService.class),
                new IFileURLBuilder<EmployeeSignature>() {
                    @Override
                    public String getUrl(EmployeeSignature employeeSignature) {
                        return MediaUtils.createEmployeeSignatureUrl(employeeSignature);
                    }
                });
        signature.setImageSize(200, 125);
        // TODO change this placeholder picture
        signature.setThumbnailPlaceholder(new Image(VistaImages.INSTANCE.profilePicture()));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().signature(), signature)).customLabel("").build());
        return main;
    }

    private TwoColumnFlexFormPanel createPrivilegesTab(String title) {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(title);

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("Information"));
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().password()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().passwordConfirm()), 10).build());
        content.setBR(++row, 0, 2);
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().enabled()), 5).build());
        content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().requiredPasswordChangeOnNextLogIn()), 5).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().isSecurityQuestionSet()), 5).build());
        content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().credentialUpdated()), 15).build());

        content.setH1(++row, 0, 2, i18n.tr("Roles"));
        content.setWidget(++row, 0, 2, inject(proto().roles(), new CrmRoleFolder(isEditable())));

        content.setH1(++row, 0, 2, i18n.tr("Buildings Access"));
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().restrictAccessToSelectedBuildingsAndPortfolios()), true).build());
        get(proto().restrictAccessToSelectedBuildingsAndPortfolios()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                buildingsAccessPanel.setVisible(event.getValue());
            }
        });

        int baRow = -1;
        buildingsAccessPanel.setH3(++baRow, 0, 2, i18n.tr("Buildings"));
        buildingsAccessPanel.setWidget(++baRow, 0, 2, inject(proto().buildingAccess(), new BuildingFolder(isEditable())));

        buildingsAccessPanel.setH3(++baRow, 0, 2, i18n.tr("Portfolios"));
        buildingsAccessPanel.setWidget(++baRow, 0, 2, inject(proto().portfolios(), new PortfolioFolder(isEditable())));

        content.setWidget(++row, 0, 2, buildingsAccessPanel);

        content.setH1(++row, 0, 2, i18n.tr("Subordinates"));
        content.setWidget(++row, 0, 2, inject(proto().employees(), new EmployeeFolder(isEditable(), new ParentEmployeeGetter() {
            @Override
            public Key getParentId() {
                return (getValue() != null ? getValue().getPrimaryKey() : null);
            }
        })));

        return content;
    }

    private TwoColumnFlexFormPanel createAuditingConfigurationTab(String title) {
        TwoColumnFlexFormPanel tabContent = new TwoColumnFlexFormPanel(title);

        tabContent.setWidget(0, 0, 2, inject(proto().userAuditingConfiguration(), new UserAuditingConfigurationForm()));

        return tabContent;
    }

    private TwoColumnFlexFormPanel createAlertsTab(String title) {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(title);

        content.setWidget(0, 0, inject(proto().notifications(), new NotificationFolder()));

        return content;
    }

    private boolean isNewEmployee() {
        return getValue().id().isNull();
    }

    private class NotificationFolder extends VistaBoxFolder<Notification> {

        public NotificationFolder() {
            super(Notification.class);
        }

        @Override
        protected void addItem() {
            new SelectEnumDialog<NotificationType>(i18n.tr("Select Notification Type"), EnumSet.allOf(NotificationType.class)) {
                @Override
                public boolean onClickOk() {
                    Notification item = EntityFactory.create(Notification.class);
                    item.type().setValue(getSelectedType());
                    addItem(item);
                    return true;
                }
            }.show();
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof Notification) {
                return new NotificationEditor();
            }
            return super.create(member);
        }

        private class NotificationEditor extends CEntityDecoratableForm<Notification> {

            public NotificationEditor() {
                super(Notification.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));
                int row = -1;

                content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().type(), new CEnumLabel()), 22, true).build());

                content.setH3(++row, 0, 1, proto().buildings().getMeta().getCaption());
                content.setWidget(++row, 0, inject(proto().buildings(), new BuildingFolder(isEditable(), EmployeeForm.this)));

                content.setH3(++row, 0, 1, proto().portfolios().getMeta().getCaption());
                content.setWidget(++row, 0, inject(proto().portfolios(), new PortfolioFolder(isEditable(), EmployeeForm.this)));

                return content;
            }
        }
    }

    public boolean isRestrictAccessSet() {
        return get(proto().restrictAccessToSelectedBuildingsAndPortfolios()).getValue();
    }

    public List<Building> getBuildingAccess() {
        if (isRestrictAccessSet()) {
            return get(proto().buildingAccess()).getValue();
        }
        return null;
    }

    public List<Portfolio> getPortfolioAccess() {
        if (isRestrictAccessSet()) {
            return get(proto().portfolios()).getValue();
        }
        return null;
    }
}
