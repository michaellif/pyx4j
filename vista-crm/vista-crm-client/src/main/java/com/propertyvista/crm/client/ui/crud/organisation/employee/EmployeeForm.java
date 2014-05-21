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
import com.pyx4j.forms.client.ui.CBooleanLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.security.UserAuditingConfigurationForm;
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
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class EmployeeForm extends CrmEntityForm<EmployeeDTO> {

    private static final I18n i18n = I18n.get(EmployeeForm.class);

    private final Tab privilegesTab, auditingTab, alertsTab;

    private final FormPanel buildingsAccessPanel;

    public EmployeeForm(IForm<EmployeeDTO> view) {
        super(EmployeeDTO.class, view);
        buildingsAccessPanel = new FormPanel(this);
        selectTab(addTab(createInfoTab(), i18n.tr("Personal Information")));
        privilegesTab = addTab(createPrivilegesTab(), i18n.tr("Privileges"));
        auditingTab = addTab(createAuditingConfigurationTab(), i18n.tr("Auditing"));
        alertsTab = addTab(createAlertsTab(), i18n.tr("Alerts"));
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().passwordConfirm()).addComponentValidator(new AbstractComponentValidator<String>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null && !getComponent().getValue().equals(get(proto().password()).getValue())) {
                    return new BasicValidationError(getComponent(), i18n.tr("The passwords don't match. Please retype the passwords."));
                }
                return null;
            }
        });

        get(proto().password()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().passwordConfirm())));

        get(proto().birthDate()).addComponentValidator(new BirthdayDateValidator());
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
        get(proto().enabled()).setVisible(isManager);
        get(proto().requiredPasswordChangeOnNextLogIn()).setVisible(isManager);

        boolean permitPortfoliosEditing = (isManager && !isSelfEditor);
        get(proto().restrictAccessToSelectedBuildingsAndPortfolios()).setEditable(permitPortfoliosEditing);
        get(proto().buildingAccess()).setEditable(permitPortfoliosEditing);
        get(proto().portfolios()).setEditable(permitPortfoliosEditing);

        get(proto().roles()).setEditable(!isSelfEditor);
        get(proto().employees()).setEditable(isManager);

        get(proto().userAuditingConfiguration()).setEnabled(isSelfEditor || isManager);

        get(proto().birthDate()).setVisible(isManager || isSelfEditor);
        get(proto().homePhone()).setVisible(isManager || isSelfEditor);
        get(proto().mobilePhone()).setVisible(isManager || isSelfEditor);
        get(proto().signature().file()).setVisible(isManager || isSelfEditor);

        privilegesTab.setTabVisible(isSelfEditor || isManager);
        auditingTab.setTabVisible(VistaTODO.VISTA_4066_EmployeeAuditingEmailNotificationsImplemented && (isSelfEditor || isManager));
        alertsTab.setTabVisible(isSelfEditor || isManager);
    }

    private IsWidget createInfoTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Right, proto().employeeId()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().title()).decorate().componentWidth(150);

        formPanel.append(Location.Dual, proto().name(), new NameEditor(i18n.tr("Employee")));
        formPanel.append(Location.Left, proto().sex()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().birthDate()).decorate().componentWidth(100);

        formPanel.br();
        formPanel.append(Location.Left, proto().homePhone()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().mobilePhone()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().workPhone()).decorate().componentWidth(100);
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
        formPanel.append(Location.Left, proto().password()).decorate();
        formPanel.append(Location.Left, proto().passwordConfirm()).decorate();
        formPanel.br();
        formPanel.append(Location.Left, proto().enabled()).decorate().componentWidth(50);
        formPanel.append(Location.Right, proto().requiredPasswordChangeOnNextLogIn()).decorate().componentWidth(50);
        formPanel.append(Location.Left, proto().isSecurityQuestionSet(), new CBooleanLabel()).decorate().componentWidth(50);
        formPanel.append(Location.Right, proto().credentialUpdated()).decorate().componentWidth(150);

        formPanel.h1(i18n.tr("Roles"));
        formPanel.append(Location.Left, proto().roles(), new CrmRoleFolder(this));

        formPanel.h1(i18n.tr("Buildings Access"));
        formPanel.append(Location.Left, proto().restrictAccessToSelectedBuildingsAndPortfolios()).decorate().componentWidth(200);
        get(proto().restrictAccessToSelectedBuildingsAndPortfolios()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                buildingsAccessPanel.setVisible(event.getValue());
            }
        });

        buildingsAccessPanel.h3(i18n.tr("Buildings"));
        buildingsAccessPanel.append(Location.Dual, proto().buildingAccess(), new BuildingFolder(this));

        buildingsAccessPanel.h3(i18n.tr("Portfolios"));
        buildingsAccessPanel.append(Location.Dual, proto().portfolios(), new PortfolioFolder(getParentView(), isEditable()));

        formPanel.append(Location.Dual, buildingsAccessPanel);

        formPanel.h1(i18n.tr("Subordinates"));
        formPanel.append(Location.Dual, proto().employees(), new EmployeeFolder(this, new ParentEmployeeGetter() {
            @Override
            public Key getParentId() {
                return (getValue() != null ? getValue().getPrimaryKey() : null);
            }
        }));

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
            if (!VistaFeatures.instance().yardiIntegration() || !SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaAccountOwner)) {
                types.remove(NotificationType.YardiSynchronization);
            }

            new SelectEnumDialog<NotificationType>(i18n.tr("Select Notification Type"), types) {
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
                formPanel.append(Location.Dual, proto().buildings(), new BuildingFolder(EmployeeForm.this));

                formPanel.h3(proto().portfolios().getMeta().getCaption());
                formPanel.append(Location.Dual, proto().portfolios(), new PortfolioFolder(EmployeeForm.this.getParentView(), EmployeeForm.this.isEditable()));

                return formPanel;
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
