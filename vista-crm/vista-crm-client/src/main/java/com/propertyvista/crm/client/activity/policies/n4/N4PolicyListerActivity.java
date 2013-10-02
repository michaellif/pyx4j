/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.n4;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.n4.N4PolicyListerView;
import com.propertyvista.crm.rpc.services.policies.policy.N4PolicyCrudService;
import com.propertyvista.domain.policy.dto.N4PolicyDTO;

public class N4PolicyListerActivity extends AbstractListerActivity<N4PolicyDTO> {

    public N4PolicyListerActivity(Place place) {
        super(place, CrmSite.getViewFactory().instantiate(N4PolicyListerView.class), GWT.<N4PolicyCrudService> create(N4PolicyCrudService.class),
                N4PolicyDTO.class);
    }

}
