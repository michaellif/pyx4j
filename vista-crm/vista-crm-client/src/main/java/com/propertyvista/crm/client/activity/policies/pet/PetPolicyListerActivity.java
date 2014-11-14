/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.pet;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.pet.PetPolicyListerView;
import com.propertyvista.domain.policy.dto.PetPolicyDTO;

public class PetPolicyListerActivity extends AbstractListerActivity<PetPolicyDTO> {

    public PetPolicyListerActivity(AppPlace place) {
        super(PetPolicyDTO.class, place, CrmSite.getViewFactory().getView(PetPolicyListerView.class));
    }

}
