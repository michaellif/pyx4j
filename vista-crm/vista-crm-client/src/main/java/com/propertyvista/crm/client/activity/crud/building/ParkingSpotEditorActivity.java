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
package com.propertyvista.crm.client.activity.crud.building;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingSpotEditorView;
import com.propertyvista.crm.rpc.services.building.ParkingSpotCrudService;
import com.propertyvista.domain.property.asset.ParkingSpot;

public class ParkingSpotEditorActivity extends CrmEditorActivity<ParkingSpot> {

    @SuppressWarnings("unchecked")
    public ParkingSpotEditorActivity(CrudAppPlace place) {
        super(ParkingSpot.class,  place, CrmSite.getViewFactory().getView(ParkingSpotEditorView.class),
                (AbstractCrudService<ParkingSpot>) GWT.create(ParkingSpotCrudService.class));
    }
}
