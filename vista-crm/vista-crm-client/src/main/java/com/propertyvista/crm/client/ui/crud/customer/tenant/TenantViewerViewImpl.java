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

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.TenantDTO;

public class TenantViewerViewImpl extends CrmViewerViewImplBase<TenantDTO> implements TenantViewerView {

    private final static I18n i18n = I18n.get(TenantViewerViewImpl.class);

    private final MenuItem passwordAction;

    private final MenuItem screeningAction;

    public TenantViewerViewImpl() {
        super(CrmSiteMap.Tenants.Tenant.class);

        //set main form here:
        setForm(new TenantForm(true));

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
    }

    @Override
    public void reset() {
        setActionVisible(passwordAction, false);
        setActionVisible(screeningAction, false);

        super.reset();
    }

    @Override
    public void populate(TenantDTO value) {
        super.populate(value);

        setActionVisible(screeningAction, value.customer().personScreening().getPrimaryKey() == null);

        // Disable password change button for tenants with no associated user principal
        if (value != null & !value.customer().user().isNull()) {
            setActionVisible(passwordAction, true);
        } else {
            setActionVisible(passwordAction, false);
        }
    }
}