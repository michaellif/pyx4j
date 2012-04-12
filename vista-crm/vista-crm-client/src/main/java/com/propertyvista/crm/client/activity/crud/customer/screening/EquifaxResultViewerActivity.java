/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.customer.screening;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.customer.screening.EquifaxResultViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.CustomerViewFactory;
import com.propertyvista.crm.rpc.services.customer.screening.EquifaxResultCrudService;
import com.propertyvista.misc.EquifaxResult;

public class EquifaxResultViewerActivity extends CrmViewerActivity<EquifaxResult> {

    @SuppressWarnings("unchecked")
    public EquifaxResultViewerActivity(Place place) {
        super(place, CustomerViewFactory.instance(EquifaxResultViewerView.class), (AbstractCrudService<EquifaxResult>) GWT.create(EquifaxResultCrudService.class));
    }
}