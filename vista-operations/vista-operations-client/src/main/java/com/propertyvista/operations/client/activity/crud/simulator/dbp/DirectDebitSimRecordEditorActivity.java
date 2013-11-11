/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.simulator.dbp;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimRecordEditorView;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimRecord;
import com.propertyvista.operations.rpc.services.simulator.DirectDebitSimRecordCrudService;

public class DirectDebitSimRecordEditorActivity extends AbstractEditorActivity<DirectDebitSimRecord> implements DirectDebitSimRecordEditorView.Presenter {

    public DirectDebitSimRecordEditorActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().getView(DirectDebitSimRecordEditorView.class), GWT
                .<DirectDebitSimRecordCrudService> create(DirectDebitSimRecordCrudService.class), DirectDebitSimRecord.class);
    }

}
