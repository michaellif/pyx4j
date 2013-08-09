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

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.dto.TenantPortalAccessInformationDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class TenantViewerViewImpl extends CrmViewerViewImplBase<TenantDTO> implements TenantViewerView {

    private final static I18n i18n = I18n.get(TenantViewerViewImpl.class);

    private final MenuItem passwordAction;

    private final MenuItem screeningAction;

    private final MenuItem maintenanceAction;

    private final MenuItem registrationAction;

    public TenantViewerViewImpl() {
        setForm(new TenantForm(this));

        addHeaderToolbarItem(new Button(i18n.tr("Maintenance Requests"), new Command() {
            @Override
            public void execute() {
                if (!isVisorShown()) {
                    ((TenantViewerView.Presenter) getPresenter()).getMaintenanceRequestVisorController().show();
                }
            }
        }));

        passwordAction = new MenuItem(i18n.tr("Change Password"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).goToChangePassword(getForm().getValue().customer().user().getPrimaryKey(), getForm().getValue()
                        .customer().person().getStringView());
            }
        });
        addAction(passwordAction);

        screeningAction = new MenuItem(i18n.tr("Create Screening"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).goToCreateScreening();
            }
        });
        addAction(screeningAction);

        maintenanceAction = new MenuItem(i18n.tr("Create Maintenance Request"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).goToCreateMaintenanceRequest();
            }
        });
        addAction(maintenanceAction);

        registrationAction = new MenuItem(i18n.tr("Portal Registration Information"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).getPortalRegistrationInformation();
            }
        });
        addAction(registrationAction);
    }

    @Override
    public void reset() {
        setActionVisible(passwordAction, false);
        setActionVisible(screeningAction, false);
        setActionVisible(maintenanceAction, false);
        setActionVisible(registrationAction, false);

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

        setActionVisible(screeningAction, value.customer().personScreening().getPrimaryKey() == null);
        setActionVisible(maintenanceAction, value.lease().status().getValue().isActive());

        // Disable password change button for tenants with no associated user principal
        if (value != null & !value.customer().user().isNull()) {
            setActionVisible(passwordAction, true);
        } else {
            setActionVisible(passwordAction, false);
        }

        if (VistaFeatures.instance().yardiIntegration()) {
            setActionVisible(maintenanceAction, !value.isPotentialTenant().isBooleanTrue());
        }

        setActionVisible(registrationAction, LeaseTermParticipant.Role.portalAccess().contains(value.role().getValue()));
    }

    @Override
    public void displayPortalRegistrationInformation(TenantPortalAccessInformationDTO info) {
        new PortalRegistrationInformationDialog(info).show();
    }

    private static final class PortalRegistrationInformationDialog extends OkDialog {

        public PortalRegistrationInformationDialog(TenantPortalAccessInformationDTO info) {
            super(i18n.tr("Portal Registration Information"));
            CEntityDecoratableForm<TenantPortalAccessInformationDTO> portalRegistrationInfoForm = new CEntityDecoratableForm<TenantPortalAccessInformationDTO>(
                    TenantPortalAccessInformationDTO.class) {
                @Override
                public IsWidget createContent() {
                    TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
                    int row = -1;
                    panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().address())).build());
                    panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().cityZip())).build());
                    panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().unit())).build());
                    panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().firstName())).build());
                    panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().middleName())).build());
                    panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().lastName())).build());
                    panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().portalRegistrationToken())).build());
                    return panel;
                }
            };
            portalRegistrationInfoForm.initContent();
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