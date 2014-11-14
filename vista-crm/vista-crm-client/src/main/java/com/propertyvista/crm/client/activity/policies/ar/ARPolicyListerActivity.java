/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.ar;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.ar.ARPolicyListerView;
import com.propertyvista.domain.policy.dto.ARPolicyDTO;

public class ARPolicyListerActivity extends AbstractListerActivity<ARPolicyDTO> {

    public ARPolicyListerActivity(AppPlace place) {
        super(ARPolicyDTO.class, place, CrmSite.getViewFactory().getView(ARPolicyListerView.class));
    }

}
