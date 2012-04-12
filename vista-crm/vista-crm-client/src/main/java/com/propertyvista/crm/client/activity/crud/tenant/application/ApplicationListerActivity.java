/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.tenant.application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;

import com.propertyvista.crm.client.ui.crud.tenant.application.ApplicationListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.services.lease.application.online.OnlineApplicationCrudService;
import com.propertyvista.dto.OnlineApplicationDTO;

public class ApplicationListerActivity extends ListerActivityBase<OnlineApplicationDTO> {

    @SuppressWarnings("unchecked")
    public ApplicationListerActivity(Place place) {
        super(place, LeaseViewFactory.instance(ApplicationListerView.class), (AbstractCrudService<OnlineApplicationDTO>) GWT
                .create(OnlineApplicationCrudService.class), OnlineApplicationDTO.class);
    }

}
