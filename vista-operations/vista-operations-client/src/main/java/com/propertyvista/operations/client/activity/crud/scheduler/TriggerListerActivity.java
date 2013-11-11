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
package com.propertyvista.operations.client.activity.crud.scheduler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.scheduler.trigger.TriggerListerView;
import com.propertyvista.operations.rpc.dto.TriggerDTO;
import com.propertyvista.operations.rpc.services.scheduler.TriggerCrudService;

public class TriggerListerActivity extends AbstractListerActivity<TriggerDTO> {

    @SuppressWarnings("unchecked")
    public TriggerListerActivity(Place place) {
        super(place, OperationsSite.getViewFactory().getView(TriggerListerView.class), (AbstractCrudService<TriggerDTO>) GWT
                .create(TriggerCrudService.class), TriggerDTO.class);
    }
}
