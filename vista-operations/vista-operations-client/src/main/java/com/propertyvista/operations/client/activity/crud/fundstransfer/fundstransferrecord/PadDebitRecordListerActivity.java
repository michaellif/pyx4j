/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-31
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.fundstransfer.fundstransferrecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityFiltersBuilder;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.fundstransfer.fundstransferrecord.PadDebitRecordListerView;
import com.propertyvista.operations.rpc.dto.PadDebitRecordDTO;
import com.propertyvista.operations.rpc.services.PadDebitRecordCrudService;

public class PadDebitRecordListerActivity extends AbstractListerActivity<PadDebitRecordDTO> {

    public PadDebitRecordListerActivity(Place place) {
        super(place, OperationsSite.getViewFactory().getView(PadDebitRecordListerView.class), GWT
                .<AbstractCrudService<PadDebitRecordDTO>> create(PadDebitRecordCrudService.class), PadDebitRecordDTO.class);
    }

    @Override
    protected void parseExternalFilters(AppPlace place, Class<PadDebitRecordDTO> entityClass, EntityFiltersBuilder<PadDebitRecordDTO> filters) {
        super.parseExternalFilters(place, entityClass, filters);

        String val;
        if ((val = place.getFirstArg(filters.proto().padBatch().padFile().id().getPath().toString())) != null) {
            filters.eq(filters.proto().padBatch().padFile().id(), new Key(val));
        }
    }

}
