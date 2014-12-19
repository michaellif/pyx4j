/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-11
 * @author ArtyomB
 */
package com.propertyvista.crm.client.activity.policies.autopaychange;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.autopaychangepolicy.AutoPayChangePolicyListerView;
import com.propertyvista.domain.policy.dto.AutoPayPolicyDTO;

public class AutoPayPolicyListerActivity extends AbstractPrimeListerActivity<AutoPayPolicyDTO> {

    public AutoPayPolicyListerActivity(AppPlace place) {
        super(AutoPayPolicyDTO.class, place, CrmSite.getViewFactory().getView(AutoPayChangePolicyListerView.class));
    }

}
