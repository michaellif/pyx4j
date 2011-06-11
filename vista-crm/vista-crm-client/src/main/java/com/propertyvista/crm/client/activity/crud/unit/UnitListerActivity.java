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
package com.propertyvista.crm.client.activity.crud.unit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.propertyvista.crm.client.activity.crud.ListerActivityBase;
import com.propertyvista.crm.client.ui.crud.unit.UnitListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.UnitViewFactory;
import com.propertyvista.crm.rpc.services.AbstractCrudService;
import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.dto.AptUnitDTO;

public class UnitListerActivity extends ListerActivityBase<AptUnitDTO> {

    @SuppressWarnings("unchecked")
    public UnitListerActivity(Place place) {
        super((UnitListerView) UnitViewFactory.instance(UnitListerView.class), (AbstractCrudService<AptUnitDTO>) GWT.create(UnitCrudService.class),
                AptUnitDTO.class);
        withPlace(place);
    }
}
