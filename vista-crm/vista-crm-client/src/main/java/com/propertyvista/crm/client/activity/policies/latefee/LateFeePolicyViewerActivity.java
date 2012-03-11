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
package com.propertyvista.crm.client.activity.policies.latefee;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.policies.latefee.LateFeePolicyViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.PolicyViewFactory;
import com.propertyvista.crm.rpc.services.policies.policy.LateFeePolicyCrudService;
import com.propertyvista.domain.policy.dto.LateFeePolicyDTO;

public class LateFeePolicyViewerActivity extends CrmViewerActivity<LateFeePolicyDTO> {

    public LateFeePolicyViewerActivity(Place place) {
        super(place, PolicyViewFactory.instance(LateFeePolicyViewerView.class), GWT.<LateFeePolicyCrudService> create(LateFeePolicyCrudService.class));
    }
}
