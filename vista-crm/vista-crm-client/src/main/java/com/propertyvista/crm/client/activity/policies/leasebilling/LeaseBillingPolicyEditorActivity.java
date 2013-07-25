/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.leasebilling;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.policies.common.PolicyEditorActivityBase;
import com.propertyvista.crm.client.ui.crud.policies.leasebilling.LeaseBillingPolicyEditorView;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseBillingPolicyCrudService;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.policy.dto.LeaseBillingPolicyDTO;

public class LeaseBillingPolicyEditorActivity extends PolicyEditorActivityBase<LeaseBillingPolicyDTO> {

    public LeaseBillingPolicyEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(LeaseBillingPolicyEditorView.class), GWT
                .<LeaseBillingPolicyCrudService> create(LeaseBillingPolicyCrudService.class), LeaseBillingPolicyDTO.class);
    }

    @Override
    protected void createNewEntity(final AsyncCallback<LeaseBillingPolicyDTO> callback) {
        super.createNewEntity(new DefaultAsyncCallback<LeaseBillingPolicyDTO>() {
            @Override
            public void onSuccess(LeaseBillingPolicyDTO entity) {
                entity.prorationMethod().setValue(BillingAccount.ProrationMethod.Standard);

                callback.onSuccess(entity);
            }
        });

    }
}
