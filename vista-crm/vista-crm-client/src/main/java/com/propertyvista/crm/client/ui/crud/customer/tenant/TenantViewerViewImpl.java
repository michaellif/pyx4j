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
package com.propertyvista.crm.client.ui.crud.customer.tenant;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button.SecureMenuItem;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.crm.rpc.services.customer.ac.TenantChangePassword;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseParticipantScreeningTO;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.dto.TenantPortalAccessInformationDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class TenantViewerViewImpl extends CrmViewerViewImplBase<TenantDTO> implements TenantViewerView {

    private final static I18n i18n = I18n.get(TenantViewerViewImpl.class);

    private final MenuItem screeningView;

    private final MenuItem maintenanceView;

    private final MenuItem deletedPapsView;

    private final MenuItem passwordAction;

    private final MenuItem registrationView;

    private final MenuItem maintenanceAction;

    public TenantViewerViewImpl() {
        setForm(new TenantForm(this));

        // Views:
        screeningView = new SecureMenuItem(i18n.tr("Screening"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).viewScreening();
            }
        }, DataModelPermission.permissionRead(LeaseParticipantScreeningTO.class));
        addView(screeningView);

        maintenanceView = new SecureMenuItem(i18n.tr("Maintenance Requests"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).getMaintenanceRequestVisorController().show();
            }
        }, DataModelPermission.permissionRead(MaintenanceRequestDTO.class));
        addView(maintenanceView);

        deletedPapsView = new SecureMenuItem(i18n.tr("Deleted AutoPayments"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).viewDeletedPaps();
            }
        }, DataModelPermission.permissionRead(AutoPayHistoryDTO.class));
        addView(deletedPapsView);

        registrationView = new SecureMenuItem(i18n.tr("Portal Registration Information"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).retrievePortalRegistrationInformation();
            }
        }, DataModelPermission.permissionRead(TenantPortalAccessInformationDTO.class));
        addView(registrationView);

        // Actions:
        passwordAction = new SecureMenuItem(i18n.tr("Change Password"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).changePassword(getForm().getValue().customer().user().getPrimaryKey(), getForm().getValue()
                        .customer().person().getStringView());
            }
        }, TenantChangePassword.class);
        addAction(passwordAction);

        maintenanceAction = new SecureMenuItem(i18n.tr("Create Maintenance Request"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).createMaintenanceRequest();
            }
        }, DataModelPermission.permissionCreate(MaintenanceRequestDTO.class));
        addAction(maintenanceAction);
    }

    @Override
    public void reset() {
        setActionVisible(screeningView, false);
        setActionVisible(deletedPapsView, false);
        setActionVisible(registrationView, false);

        setActionVisible(passwordAction, false);
        setActionVisible(maintenanceAction, false);

        super.reset();
    }

    @Override
    public void populate(TenantDTO value) {
        // tweak legal naming:
        if (value.lease().status().getValue().isDraft()) {
            setCaptionBase(i18n.tr("Applicant"));
        } else {
            setCaptionBase(i18n.tr("Tenant"));
        }

        super.populate(value);

        setViewVisible(screeningView, value.screening().getPrimaryKey() != null);

        setActionVisible(maintenanceView, !value.lease().status().getValue().isDraft());
        setActionVisible(maintenanceAction, canCreateMaintenance(value));

        boolean hasPortalAccess = LeaseTermParticipant.Role.portalAccess().contains(value.role().getValue());

        // Disable password change button for tenants with no associated user principal (+ regular portal access rule):
        setActionVisible(passwordAction, hasPortalAccess && !value.customer().user().isNull());
        setActionVisible(registrationView, hasPortalAccess && !value.customer().registeredInPortal().getValue(Boolean.FALSE));

        setViewVisible(deletedPapsView, value.lease().status().getValue().isCurrent());
    }

    public boolean canCreateMaintenance(TenantDTO value) {
        if (VistaFeatures.instance().yardiIntegration()) {
            return (value.lease().status().getValue().isActive() && !value.isPotentialTenant().getValue(false));
        } else {
            return value.lease().status().getValue().isActive();
        }
    }

    @Override
    public void displayPortalRegistrationInformation(TenantPortalAccessInformationDTO info) {
        new PortalRegistrationInformationDialog(info).show();
    }

    private static final class PortalRegistrationInformationDialog extends OkDialog {

        public PortalRegistrationInformationDialog(TenantPortalAccessInformationDTO info) {
            super(i18n.tr("Portal Registration Information"));
            CForm<TenantPortalAccessInformationDTO> portalRegistrationInfoForm = new CForm<TenantPortalAccessInformationDTO>(
                    TenantPortalAccessInformationDTO.class) {
                @Override
                protected IsWidget createContent() {
                    FormPanel panel = new FormPanel(this);

                    panel.append(Location.Left, proto().address()).decorate();
                    panel.append(Location.Left, proto().postalCode()).decorate();
                    panel.append(Location.Left, proto().unit()).decorate();
                    panel.append(Location.Left, proto().firstName()).decorate();
                    panel.append(Location.Left, proto().middleName()).decorate();
                    panel.append(Location.Left, proto().lastName()).decorate();
                    panel.append(Location.Left, proto().portalRegistrationToken()).decorate();

                    return panel;
                }
            };
            portalRegistrationInfoForm.init();
            portalRegistrationInfoForm.setViewable(true);
            portalRegistrationInfoForm.populate(info);
            setBody(portalRegistrationInfoForm);
        }

        @Override
        public boolean onClickOk() {
            return true;
        }

    }
}