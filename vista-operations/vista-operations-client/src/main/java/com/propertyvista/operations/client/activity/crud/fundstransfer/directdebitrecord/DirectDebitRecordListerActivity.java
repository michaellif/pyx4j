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
package com.propertyvista.operations.client.activity.crud.fundstransfer.directdebitrecord;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.fundstransfer.directdebitrecords.DirectDebitRecordListerView;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;

public class DirectDebitRecordListerActivity extends AbstractPrimeListerActivity<DirectDebitRecord> {

    public DirectDebitRecordListerActivity(AppPlace place) {
        super(DirectDebitRecord.class, place, OperationsSite.getViewFactory().getView(DirectDebitRecordListerView.class));
    }

}
