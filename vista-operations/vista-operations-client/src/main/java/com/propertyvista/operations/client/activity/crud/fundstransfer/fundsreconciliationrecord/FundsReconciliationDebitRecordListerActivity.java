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
package com.propertyvista.operations.client.activity.crud.fundstransfer.fundsreconciliationrecord;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityFiltersBuilder;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.fundstransfer.fundsreconciliationrecord.FundsReconciliationDebitRecordListerView;
import com.propertyvista.operations.rpc.dto.FundsReconciliationRecordRecordDTO;

public class FundsReconciliationDebitRecordListerActivity extends AbstractListerActivity<FundsReconciliationRecordRecordDTO> {

    public FundsReconciliationDebitRecordListerActivity(AppPlace place) {
        super(FundsReconciliationRecordRecordDTO.class, place, OperationsSite.getViewFactory().getView(FundsReconciliationDebitRecordListerView.class));
    }

    @Override
    protected void parseExternalFilters(AppPlace place, Class<FundsReconciliationRecordRecordDTO> entityClass,
            EntityFiltersBuilder<FundsReconciliationRecordRecordDTO> filters) {
        String val;
        if ((val = place.getFirstArg(filters.proto().reconciliationSummary().reconciliationFile().id().getPath().toString())) != null) {
            filters.eq(filters.proto().reconciliationSummary().reconciliationFile().id(), new Key(val));

        }
        // Actually super() of this method should do this, but it's bugged
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_PARENT_ID)) != null) {
            filters.eq(filters.proto().reconciliationSummary().id(), new Key(val));
        }

    }

}
