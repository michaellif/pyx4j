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

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.OkDialog;

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

    private final MenuItem viewDeletedPapsAction;

    public TenantViewerViewImpl() {
        setForm(new TenantForm(this));

        passwordAction = new MenuItem(i18n.tr("Change Password"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).changePassword(getForm().getValue().customer().user().getPrimaryKey(), getForm().getValue()
                        .customer().person().getStringView());
            }
        });
        addAction(passwordAction);

        registrationAction = new MenuItem(i18n.tr("Portal Registration Information"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).retrievePortalRegistrationInformation();
            }
        });
        addAction(registrationAction);

        screeningAction = new MenuItem(i18n.tr("Create Screening"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).createScreening();
            }
        });
        addAction(screeningAction);

        maintenanceAction = new MenuItem(i18n.tr("Create Maintenance Request"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).createMaintenanceRequest();
            }
        });
        addAction(maintenanceAction);

        viewDeletedPapsAction = new MenuItem(i18n.tr("View Deleted AutoPayments"), new Command() {
            @Override
            public void execute() {
                ((TenantViewerView.Presenter) getPresenter()).viewDeletedPaps();
            }
        });
        addAction(viewDeletedPapsAction);

        // ------------------------------------------------------------------------------------------------------------

        addHeaderToolbarItem(new Button(i18n.tr("Maintenance Requests"), new Command() {
            @Override
            public void execute() {
                if (!isVisorShown()) {
                    ((TenantViewerView.Presenter) getPresenter()).getMaintenanceRequestVisorController().show();
                }
            }
        }));
    }

    @Override
    public void reset() {
        setActionVisible(passwordAction, false);
        setActionVisible(screeningAction, false);
        setActionVisible(maintenanceAction, false);
        setActionVisible(registrationAction, false);
        setActionVisible(viewDeletedPapsAction, false);

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

        boolean leaseIsActive = value.lease().status().getValue().isActive();

        setActionVisible(screeningAction, value.screening().getPrimaryKey() == null);
        if (VistaFeatures.instance().yardiIntegration()) {
            setActionVisible(maintenanceAction, leaseIsActive && !value.isPotentialTenant().getValue(false));
        } else {
            setActionVisible(maintenanceAction, leaseIsActive);
        }

        boolean hasPortalAccess = LeaseTermParticipant.Role.portalAccess().contains(value.role().getValue());

        // Disable password change button for tenants with no associated user principal (+ regular portal access rule):
        setActionVisible(passwordAction, hasPortalAccess && !value.customer().user().isNull());
        setActionVisible(registrationAction, hasPortalAccess && !value.customer().registeredInPortal().getValue(Boolean.FALSE));

        setActionVisible(viewDeletedPapsAction, leaseIsActive);
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
                    BasicFlexFormPanel panel = new BasicFlexFormPanel();
                    int row = -1;
                    panel.setWidget(++row, 0, inject(proto().address(), new FieldDecoratorBuilder().build()));
                    panel.setWidget(++row, 0, inject(proto().postalCode(), new FieldDecoratorBuilder().build()));
                    panel.setWidget(++row, 0, inject(proto().unit(), new FieldDecoratorBuilder().build()));
                    panel.setWidget(++row, 0, inject(proto().firstName(), new FieldDecoratorBuilder().build()));
                    panel.setWidget(++row, 0, inject(proto().middleName(), new FieldDecoratorBuilder().build()));
                    panel.setWidget(++row, 0, inject(proto().lastName(), new FieldDecoratorBuilder().build()));
                    panel.setWidget(++row, 0, inject(proto().portalRegistrationToken(), new FieldDecoratorBuilder().build()));
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