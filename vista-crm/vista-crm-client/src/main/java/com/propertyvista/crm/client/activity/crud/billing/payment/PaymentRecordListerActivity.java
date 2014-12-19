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
 */
package com.propertyvista.crm.client.activity.crud.billing.payment;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityFiltersBuilder;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.billing.payment.PaymentRecordListerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentRecordListerActivity extends AbstractPrimeListerActivity<PaymentRecordDTO> {

    public PaymentRecordListerActivity(AppPlace place) {
        super(PaymentRecordDTO.class, place, CrmSite.getViewFactory().getView(PaymentRecordListerView.class));

    }

    @Override
    protected void parseExternalFilters(AppPlace place, Class<PaymentRecordDTO> entityClass, EntityFiltersBuilder<PaymentRecordDTO> filters) {
        super.parseExternalFilters(place, entityClass, filters);

        String val;
        if ((val = place.getFirstArg(CrmSiteMap.Finance.BillingCycle.ARG_BC_ID)) != null) {
            filters.eq(filters.proto().padBillingCycle().id(), new Key(val));
        }
    }
}
