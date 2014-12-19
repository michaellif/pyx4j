/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author ArtyomB
 */
package com.propertyvista.crm.client.activity.policies.leaseterms;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LegalTermsPolicyListerView;
import com.propertyvista.domain.policy.dto.LegalTermsPolicyDTO;

public class LegalTermsPolicyListerActivity extends AbstractPrimeListerActivity<LegalTermsPolicyDTO> {

    public LegalTermsPolicyListerActivity(AppPlace place) {
        super(LegalTermsPolicyDTO.class, place, CrmSite.getViewFactory().getView(LegalTermsPolicyListerView.class));
    }

}
