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
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingEditorView;
import com.propertyvista.crm.rpc.services.building.ParkingCrudService;
import com.propertyvista.dto.ParkingDTO;

public class ParkingEditorActivity extends CrmEditorActivity<ParkingDTO> implements ParkingEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public ParkingEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(ParkingEditorView.class), (AbstractCrudService<ParkingDTO>) GWT.create(ParkingCrudService.class),
                ParkingDTO.class);
    }

    @Override
    protected void createNewEntity(AsyncCallback<ParkingDTO> callback) {
        ParkingDTO parking = EntityFactory.create(getEntityClass());

        // do not allow null members!
        parking.totalSpaces().setValue(0);
        parking.disabledSpaces().setValue(0);
        parking.regularSpaces().setValue(0);
        parking.wideSpaces().setValue(0);
        parking.narrowSpaces().setValue(0);

        callback.onSuccess(parking);
    }
}
