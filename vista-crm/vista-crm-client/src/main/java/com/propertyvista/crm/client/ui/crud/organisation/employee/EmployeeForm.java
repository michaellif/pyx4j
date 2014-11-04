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

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.CBooleanLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.security.UserAuditingConfigurationForm;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.crm.client.ui.components.CrmRoleFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.organisation.common.BuildingFolder;
import com.propertyvista.crm.client.ui.crud.organisation.common.PortfolioFolder;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeFolder.ParentEmployeeGetter;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.dto.company.EmployeePrivilegesDTO;
import com.propertyvista.crm.rpc.dto.company.ac.CRMUserSecurityActions;
import com.propertyvista.crm.rpc.services.organization.EmployeeSignatureUploadService;
import com.propertyvista.domain.company.EmployeeSignature;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.company.Notification.NotificationType;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.UserAuditingConfigurationDTO;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class EmployeeForm extends CrmEntityForm<EmployeeDTO> {

    private static final I18n i18n = I18n.get(EmployeeForm.class);

    private final Tab personalInfoTab, privilegesTab, auditingTab, alertsTab;

    private final FormPanel buildingsAccessPanel;

    public EmployeeForm(IForm<EmployeeDTO> view) {
        super(EmployeeDTO.class, view);
        buildingsAccessPanel = new FormPanel(this);

        selectTab(personalInfoTab = addTab(createInfoTab(), i18n.tr("Personal Information"), DataModelPermission.permissionRead(EmployeeDTO.class)));
        privilegesTab = addTab(createPrivilegesTab(), i18n.tr("Privileges"), DataModelPermission.permissionRead(EmployeePrivilegesDTO.class));
        auditingTab = addTab(createAuditingConfigurationTab(), i18n.tr("Auditing"), DataModelPermission.permissionRead(UserAuditingConfigurationDTO.class));
        alertsTab = addTab(createAlertsTab(), i18n.tr("Alerts"), DataModelPermission.permissionRead(Notification.class));

        if (isEditable()) {
            personalInfoTab.setPermitEnabledPermission(DataModelPermission.permissionUpdate(EmployeeDTO.class));
            privilegesTab.setPermitEnabledPermission(DataModelPermission.permissionUpdate(EmployeePrivilegesDTO.class));
            auditingTab.setPermitEnabledPermission(DataModelPermission.permissionUpdate(UserAuditingConfigurationDTO.class));
            alertsTab.setPermitEnabledPermission(DataModelPermission.permissionUpdate(Notification.class));
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().privileges().passwordConfirm()).addComponentValidator(new AbstractComponentValidator<String>() {
            @Override
            public BasicValidationError isValid() {
                if (getCComponent().getValue() != null && !getCComponent().getValue().equals(get(proto().privileges().password()).getValue())) {
                    return new BasicValidationError(getCComponent(), i18n.tr("The passwords don't match. Please retype the passwords."));
                }
                return null;
            }
        });

        get(proto().privileges().password()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().privileges().passwordConfirm())));

        get(proto().birthDate()).addComponentValidator(new BirthdayDateValidator());
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.employee, get(proto().employeeId()), getValue().getPrimaryKey());
        }

        get(proto().privileges().password()).setVisible(isNewEmployee());
        get(proto().privileges().passwordConfirm()).setVisible(isNewEmployee());

        buildingsAccessPanel.setVisible(getValue().privileges().restrictAccessToSelectedBuildingsAndPortfolios().getValue(false));
    }

    public void restrictSecurityRelatedControls(boolean isManager, boolean isSelfEditor) {
        get(proto().privileges().enabled()).setVisible(SecurityController.check(new ActionPermission(CRMUserSecurityActions.class)));
        get(proto().privileges().changePassword()).setVisible(SecurityController.check(new ActionPermission(CRMUserSecurityActions.class)));

        boolean permitPortfoliosEditing = (isManager && !isSelfEditor);
        get(proto().privileges().restrictAccessToSelectedBuildingsAndPortfolios()).setEditable(permitPortfoliosEditing);
        get(proto().buildingAccess()).setEditable(permitPortfoliosEditing);
        get(proto().portfolios()).setEditable(permitPortfoliosEditing);

        get(proto().privileges().roles()).setEditable(!isSelfEditor);
        get(proto().employees()).setEditable(isManager);

        get(proto().userAuditingConfiguration()).setEnabled(isSelfEditor || isManager);

        get(proto().birthDate()).setVisible(isManager || isSelfEditor);
        get(proto().homePhone()).setVisible(isManager || isSelfEditor);
        get(proto().mobilePhone()).setVisible(isManager || isSelfEditor);
        get(proto().signature().file()).setVisible(isManager || isSelfEditor);

        privilegesTab.setTabVisible(isManager);
        auditingTab.setTabVisible(VistaTODO.VISTA_4066_EmployeeAuditingEmailNotificationsImplemented && (isSelfEditor || isManager));
    }

    private IsWidget createInfoTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Right, proto().employeeId()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().title()).decorate().componentWidth(150);

        formPanel.append(Location.Dual, proto().name(), new NameEditor(i18n.tr("Employee")));
        formPanel.append(Location.Left, proto().sex()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().birthDate()).decorate().componentWidth(100);

        formPanel.br();
        formPanel.append(Location.Left, proto().homePhone()).decorate().componentWidth(200);
        formPanel.append(Location.Right, proto().mobilePhone()).decorate().componentWidth(200);

        formPanel.append(Location.Left, proto().workPhone()).decorate().componentWidth(200);
        formPanel.append(Location.Right, proto().email()).decorate().componentWidth(200);
        get(proto().email()).setMandatory(true);

        CImage signature = new CImage(GWT.<EmployeeSignatureUploadService> create(EmployeeSignatureUploadService.class), new VistaFileURLBuilder(
                EmployeeSignature.class));
        signature.setScaleMode(ScaleMode.Contain);
        signature.setImageSize(250, 60);
        signature.setThumbnailPlaceholder(new Image(VistaImages.INSTANCE.signaturePlaceholder()));

        formPanel.append(Location.Dual, proto().signature().file(), signature).decorate().customLabel(i18n.tr("Signature"));
        formPanel.append(Location.Dual, proto().description()).decorate();

        return formPanel;
    }

    private IsWidget createPrivilegesTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Information"));
        formPanel.append(Location.Left, proto().privileges().password()).decorate();
        formPanel.append(Location.Left, proto().privileges().passwordConfirm()).decorate();
        formPanel.br();
        formPanel.append(Location.Left, proto().privileges().enabled()).decorate().componentWidth("auto");
        formPanel.append(Location.Right, proto().privileges().changePassword()).decorate().componentWidth("auto");
        formPanel.append(Location.Left, proto().privileges().isSecurityQuestionSet(), new CBooleanLabel()).decorate().componentWidth("auto");
        formPanel.append(Location.Right, proto().privileges().credentialUpdated()).decorate().componentWidth(150);

        formPanel.h1(i18n.tr("Roles"));
        formPanel.append(Location.Dual, proto().privileges().roles(), new CrmRoleFolder(this));

        formPanel.h1(i18n.tr("Buildings Access"));
        formPanel.append(Location.Left, proto().privileges().restrictAccessToSelectedBuildingsAndPortfolios()).decorate().componentWidth(200);
        get(proto().privileges().restrictAccessToSelectedBuildingsAndPortfolios()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                buildingsAccessPanel.setVisible(event.getValue());
            }
        });

        buildingsAccessPanel.h3(i18n.tr("Buildings"));
        buildingsAccessPanel.append(Location.Dual, proto().buildingAccess(), new BuildingFolder(getParentView(), isEditable()));

        buildingsAccessPanel.h3(i18n.tr("Portfolios"));
        buildingsAccessPanel.append(Location.Dual, proto().portfolios(), new PortfolioFolder(getParentView(), isEditable()));

        formPanel.append(Location.Dual, buildingsAccessPanel);

        formPanel.h1(i18n.tr("Subordinates"));
        formPanel.append(Location.Dual, proto().employees(), new EmployeeFolder(this, new ParentEmployeeGetter() {
            @Override
            public Key getParentId() {
                return (getValue() != null ? getValue().getPrimaryKey() : null);
            }
        }, false));

        return formPanel;
    }

    private IsWidget createAuditingConfigurationTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().userAuditingConfiguration(), new UserAuditingConfigurationForm());

        return formPanel;
    }

    private IsWidget createAlertsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().notifications(), new NotificationFolder());

        return formPanel;
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

            Collection<NotificationType> types = EnumSet.allOf(NotificationType.class);
            if (VistaFeatures.instance().yardiIntegration()) {
                types.remove(NotificationType.BillingAlert);
            } else {
                if (!SecurityController.check(VistaCrmBehavior.AdminGeneral)) {
                    types.remove(NotificationType.YardiSynchronization);
                }
            }

            new SelectEnumDialog<NotificationType>(i18n.tr("Select Alert Type"), types) {
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
        protected CForm<Notification> createItemForm(IObject<?> member) {
            return new NotificationEditor();
        }

        private class NotificationEditor extends CForm<Notification> {

            public NotificationEditor() {
                super(Notification.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);
                formPanel.append(Location.Left, proto().type(), new CEnumLabel()).decorate().componentWidth(200);

                formPanel.h3(proto().buildings().getMeta().getCaption());
                formPanel.append(Location.Dual, proto().buildings(), new BuildingFolder(EmployeeForm.this.getParentView(), EmployeeForm.this.isEditable()));

                formPanel.h3(proto().portfolios().getMeta().getCaption());
                formPanel.append(Location.Dual, proto().portfolios(), new PortfolioFolder(EmployeeForm.this.getParentView(), EmployeeForm.this.isEditable()));

                return formPanel;
            }
        }
    }

    public boolean isRestrictAccessSet() {
        return get(proto().privileges().restrictAccessToSelectedBuildingsAndPortfolios()).getValue();
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
