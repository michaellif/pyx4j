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
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.CHyperlink;

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerViewImpl extends CrmViewerViewImplBase<LeaseDTO> implements LeaseViewerView {

    private final CHyperlink createApplicationAction;

    public LeaseViewerViewImpl() {
        super(CrmSiteMap.Tenants.Lease.class);

        createApplicationAction = new CHyperlink(new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) presenter).createMasterApplication();
            }
        });
        createApplicationAction.setValue(i18n.tr("Create Application"));
        addActionWidget(createApplicationAction.asWidget());

        // set main form here: 
        setForm(new LeaseEditorForm(new CrmViewersComponentFactory()));
    }

    @Override
    public void populate(LeaseDTO value) {
        createApplicationAction.setVisible(!value.id().isNull() && Lease.Status.Draft.equals(value.status().getValue()));
        super.populate(value);
    }
}