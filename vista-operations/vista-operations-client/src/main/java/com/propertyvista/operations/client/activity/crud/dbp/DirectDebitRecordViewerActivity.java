/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.dbp;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.AbstractViewerActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.directdebitrecords.DirectDebitRecordViewerView;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitRecord;
import com.propertyvista.operations.rpc.services.PmcDirectDebitRecordCrudService;

public class DirectDebitRecordViewerActivity extends AbstractViewerActivity<DirectDebitRecord> implements DirectDebitRecordViewerView.Presenter {

    public DirectDebitRecordViewerActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().instantiate(DirectDebitRecordViewerView.class), GWT
                .<AbstractCrudService<DirectDebitRecord>> create(PmcDirectDebitRecordCrudService.class));
    }

    @Override
    public boolean canEdit() {
        return false;
    }

}
