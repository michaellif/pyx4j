/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.billing.payment;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.criterion.EntityFiltersBuilder;
import com.pyx4j.site.client.activity.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.billing.transfer.PaymentRecordListerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.financial.PaymentRecordListService;
import com.propertyvista.domain.financial.PaymentRecord;

public class PaymentListerActivity extends AbstractListerActivity<PaymentRecord> {

    public PaymentListerActivity(AppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(PaymentRecordListerView.class), GWT.<PaymentRecordListService> create(PaymentRecordListService.class),
                PaymentRecord.class);

    }

    @Override
    protected void parseExternalFilters(AppPlace place, Class<PaymentRecord> entityClass, EntityFiltersBuilder<PaymentRecord> filters) {
        super.parseExternalFilters(place, entityClass, filters);

        String val;
        if ((val = place.getFirstArg(CrmSiteMap.Finance.BillingCycle.ARG_BC_ID)) != null) {
            filters.eq(filters.proto().padBillingCycle().id(), new Key(val));
        }
    }

}
