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
 */
package com.propertyvista.crm.client.ui.crud.billing.adjustments;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button.SecureMenuItem;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.Status;

public class LeaseAdjustmentViewerViewImpl extends CrmViewerViewImplBase<LeaseAdjustment> implements LeaseAdjustmentViewerView {

    private static final I18n i18n = I18n.get(LeaseAdjustmentViewerViewImpl.class);

    private final MenuItem submitAction;

    public LeaseAdjustmentViewerViewImpl() {
        setForm(new LeaseAdjustmentForm(this));

        submitAction = new SecureMenuItem(i18n.tr("Submit"), new Command() {
            @Override
            public void execute() {
                ((LeaseAdjustmentViewerView.Presenter) getPresenter()).submitAdjustment();
            }
        }, DataModelPermission.permissionUpdate(LeaseAdjustment.class));
        addAction(submitAction);
    }

    @Override
    public void reset() {
        setActionVisible(submitAction, false);
        super.reset();
    }

    @Override
    public void populate(LeaseAdjustment value) {
        super.populate(value);

        setActionVisible(submitAction, value.status().getValue() != Status.submited);
        setActionHighlighted(submitAction, value.status().getValue() != Status.submited);

        // enable editing for draft items only:
        setEditingVisible(value.status().getValue() == Status.draft);
    }
}