/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.yardiinterface;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.crm.client.ui.crud.policies.yardiinterface.YardiInterfacePolicyListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.PolicyViewFactory;
import com.propertyvista.crm.rpc.services.policies.policy.YardiInterfacePolicyCrudService;
import com.propertyvista.domain.policy.dto.YardiInterfacePolicyDTO;

public class YardiInterfacePolicyListerActivity extends AbstractListerActivity<YardiInterfacePolicyDTO> {

    public YardiInterfacePolicyListerActivity(Place place) {
        super(place, PolicyViewFactory.instance(YardiInterfacePolicyListerView.class), GWT
                .<YardiInterfacePolicyCrudService> create(YardiInterfacePolicyCrudService.class), YardiInterfacePolicyDTO.class);
    }

}
