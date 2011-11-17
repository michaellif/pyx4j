/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;

import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceTypeViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.ServiceItemTypeCrudService;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ServiceItemTypeViewerActivity extends ViewerActivityBase<ServiceItemType> {

    @SuppressWarnings("unchecked")
    public ServiceItemTypeViewerActivity(Place place) {
        super(place, SettingsViewFactory.instance(ServiceTypeViewerView.class), (AbstractCrudService<ServiceItemType>) GWT
                .create(ServiceItemTypeCrudService.class));
    }
}
