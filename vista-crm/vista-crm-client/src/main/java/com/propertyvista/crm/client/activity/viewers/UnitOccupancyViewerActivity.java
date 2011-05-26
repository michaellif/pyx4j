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
package com.propertyvista.crm.client.activity.viewers;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

import com.propertyvista.crm.client.ui.vewers.UnitOccupancyViewerView;
import com.propertyvista.crm.rpc.services.AbstractCrudService;
import com.propertyvista.crm.rpc.services.UnitOccupancyCrudService;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;

public class UnitOccupancyViewerActivity extends ViewerActivityBase<AptUnitOccupancy> {

    @Inject
    @SuppressWarnings("unchecked")
    public UnitOccupancyViewerActivity(UnitOccupancyViewerView view) {
        super(view, (AbstractCrudService<AptUnitOccupancy>) GWT.create(UnitOccupancyCrudService.class));
    }

}