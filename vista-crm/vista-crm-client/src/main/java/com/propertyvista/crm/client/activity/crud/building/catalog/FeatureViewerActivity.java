/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.building.catalog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.building.catalog.feature.FeatureViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.FeatureCrudService;
import com.propertyvista.domain.financial.offering.Feature;

public class FeatureViewerActivity extends ViewerActivityBase<Feature> {

    @SuppressWarnings("unchecked")
    public FeatureViewerActivity(Place place) {
        super((FeatureViewerView) MarketingViewFactory.instance(FeatureViewerView.class), (AbstractCrudService<Feature>) GWT.create(FeatureCrudService.class));
        setPlace(place);
    }
}
