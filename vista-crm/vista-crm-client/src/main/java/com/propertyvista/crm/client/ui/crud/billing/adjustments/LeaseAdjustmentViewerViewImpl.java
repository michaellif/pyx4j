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
package com.propertyvista.crm.client.ui.crud.billing.adjustments;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.Status;

public class LeaseAdjustmentViewerViewImpl extends CrmViewerViewImplBase<LeaseAdjustment> implements LeaseAdjustmentViewerView {

    private static final I18n i18n = I18n.get(LeaseAdjustmentViewerViewImpl.class);

    private final Button submitAction;

    public LeaseAdjustmentViewerViewImpl() {
        super(CrmSiteMap.Tenants.LeaseAdjustment.class, new LeaseAdjustmentForm(true));

        submitAction = new Button(i18n.tr("Submit"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseAdjustmentViewerView.Presenter) presenter).submitAdjustment();
            }
        });
        addHeaderToolbarTwoItem(submitAction.asWidget());
    }

    @Override
    public void reset() {
        submitAction.setVisible(false);
        super.reset();
    }

    @Override
    public void populate(LeaseAdjustment value) {
        super.populate(value);

        submitAction.setVisible(value.status().getValue() != Status.submited);

        // enable editing for draft items only:
        getEditButton().setVisible(value.status().getValue() == Status.draft);
    }
}