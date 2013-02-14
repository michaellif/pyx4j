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
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.TenantDTO;

public class TenantViewerViewImpl extends CrmViewerViewImplBase<TenantDTO> implements TenantViewerView {

    private final static I18n i18n = I18n.get(TenantViewerViewImpl.class);

    private final MenuItem passwordAction;

    private final MenuItem screeningAction;

    private final MenuItem maintenanceAction;

    public TenantViewerViewImpl() {
        super(CrmSiteMap.Tenants.Tenant.class);

        //set main form here:
        setForm(new TenantForm(this));

        addHeaderToolbarItem(new Button(i18n.tr("Maintenance Requests"), new Command() {
            @Override
            public void execute() {
                if (!isVisorShown()) {
                    ((TenantViewerView.Presenter) getPresenter()).getMaintenanceRequestVisorController().show(TenantViewerViewImpl.this);
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
    }

    @Override
    public void reset() {
        setActionVisible(passwordAction, false);
        setActionVisible(screeningAction, false);
        setActionVisible(maintenanceAction, false);

        super.reset();
    }

    @Override
    public void populate(TenantDTO value) {
        // tweak legal naming: 
        if (value.lease().status().getValue().isDraft()) {
            captionBase = i18n.tr("Applicant");
        } else {
            captionBase = i18n.tr("Tenant");
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
    }
}