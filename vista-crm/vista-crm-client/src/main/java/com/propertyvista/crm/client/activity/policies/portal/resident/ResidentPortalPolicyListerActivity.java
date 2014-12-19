/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 5, 2014
 * @author vlads
 */
package com.propertyvista.crm.client.activity.policies.portal.resident;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.portal.resident.ResidentPortalPolicyListerView;
import com.propertyvista.domain.policy.dto.ResidentPortalPolicyDTO;

public class ResidentPortalPolicyListerActivity extends AbstractPrimeListerActivity<ResidentPortalPolicyDTO> {

    public ResidentPortalPolicyListerActivity(AppPlace place) {
        super(ResidentPortalPolicyDTO.class, place, CrmSite.getViewFactory().getView(ResidentPortalPolicyListerView.class));
    }

}
