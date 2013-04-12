/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-21
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.pad;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.crm.client.ui.crud.policies.pad.PADPolicyListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.PolicyViewFactory;
import com.propertyvista.crm.rpc.services.policies.policy.PADPolicyCrudService;
import com.propertyvista.domain.policy.dto.PADPolicyDTO;

public class PADPolicyListerActivity extends AbstractListerActivity<PADPolicyDTO> {

    public PADPolicyListerActivity(Place place) {
        super(place, PolicyViewFactory.instance(PADPolicyListerView.class), GWT.<PADPolicyCrudService> create(PADPolicyCrudService.class), PADPolicyDTO.class);
    }

}
