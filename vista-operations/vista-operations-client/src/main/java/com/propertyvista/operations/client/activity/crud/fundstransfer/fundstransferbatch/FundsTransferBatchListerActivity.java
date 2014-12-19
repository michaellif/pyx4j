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
 */
package com.propertyvista.operations.client.activity.crud.fundstransfer.fundstransferbatch;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityFiltersBuilder;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.fundstransfer.fundstransferbatch.FundsTransferBatchListerView;
import com.propertyvista.operations.rpc.dto.FundsTransferBatchDTO;

public class FundsTransferBatchListerActivity extends AbstractPrimeListerActivity<FundsTransferBatchDTO> {

    public FundsTransferBatchListerActivity(AppPlace place) {
        super(FundsTransferBatchDTO.class, place, OperationsSite.getViewFactory().getView(FundsTransferBatchListerView.class));
    }

    @Override
    protected void parseExternalFilters(AppPlace place, Class<FundsTransferBatchDTO> entityClass, EntityFiltersBuilder<FundsTransferBatchDTO> filters) {
        super.parseExternalFilters(place, entityClass, filters);

        String val;
        if ((val = place.getFirstArg(filters.proto().padFile().id().getPath().toString())) != null) {
            filters.eq(filters.proto().padFile().id(), new Key(val));
        }
    }

}
