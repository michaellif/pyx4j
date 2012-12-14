/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings.tax;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.site.client.activity.ListerActivityBase;

import com.propertyvista.crm.client.ui.crud.settings.financial.leaseadjustmentreason.LeaseAdjustmentReasonListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.admin.LeaseAdjustmentReasonCrudService;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class LeaseAdjustmentReasonListerActivity extends ListerActivityBase<LeaseAdjustmentReason> {

    public LeaseAdjustmentReasonListerActivity(Place place) {
        super(place, SettingsViewFactory.instance(LeaseAdjustmentReasonListerView.class), GWT
                .<AbstractListService<LeaseAdjustmentReason>> create(LeaseAdjustmentReasonCrudService.class), LeaseAdjustmentReason.class);
    }
}
