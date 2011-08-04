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
package com.propertyvista.crm.client.activity.crud.marketing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.marketing.ConcessionViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.UnitViewFactory;
import com.propertyvista.crm.rpc.services.ConcessionCrudService;
import com.propertyvista.domain.financial.offering.Concession;

public class ConcessionViewerActivity extends ViewerActivityBase<Concession> {

    @SuppressWarnings("unchecked")
    public ConcessionViewerActivity(Place place) {
        super((ConcessionViewerView) UnitViewFactory.instance(ConcessionViewerView.class), (AbstractCrudService<Concession>) GWT
                .create(ConcessionCrudService.class));
        withPlace(place);
    }
}
