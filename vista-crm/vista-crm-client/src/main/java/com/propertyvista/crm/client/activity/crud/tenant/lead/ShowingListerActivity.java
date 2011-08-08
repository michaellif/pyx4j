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
package com.propertyvista.crm.client.activity.crud.tenant.lead;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.tenant.lead.ShowingListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.ShowingCrudService;
import com.propertyvista.domain.tenant.lead.Showing;

public class ShowingListerActivity extends ListerActivityBase<Showing> {

    @SuppressWarnings("unchecked")
    public ShowingListerActivity(Place place) {
        super((ShowingListerView) TenantViewFactory.instance(ShowingListerView.class), (AbstractCrudService<Showing>) GWT.create(ShowingCrudService.class),
                Showing.class);
        withPlace(place);
    }
}
