/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.tenant.screening;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.tenant.screening.PersonScreeningViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.ClientViewFactory;
import com.propertyvista.crm.rpc.services.customer.screening.PersonScreeningCrudService;
import com.propertyvista.domain.tenant.PersonScreening;

public class PersonScreeningViewerActivity extends CrmViewerActivity<PersonScreening> {

    @SuppressWarnings("unchecked")
    public PersonScreeningViewerActivity(Place place) {
        super(place, ClientViewFactory.instance(PersonScreeningViewerView.class), (AbstractCrudService<PersonScreening>) GWT
                .create(PersonScreeningCrudService.class));
    }
}
