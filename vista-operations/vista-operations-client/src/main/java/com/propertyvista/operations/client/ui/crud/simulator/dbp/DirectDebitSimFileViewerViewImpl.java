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
package com.propertyvista.operations.client.ui.crud.simulator.dbp;

import com.google.gwt.user.client.Command;

import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile;

public class DirectDebitSimFileViewerViewImpl extends OperationsViewerViewImplBase<DirectDebitSimFile> implements DirectDebitSimFileViewerView {

    public DirectDebitSimFileViewerViewImpl() {
        setForm(new DirectDebitSimFileForm(this));

        Button addNewRecordActionButton = new Button("Add new record", new Command() {
            @Override
            public void execute() {
                ((DirectDebitSimFileViewerView.Presenter) getPresenter()).addNewRecord();
            }
        });
        addHeaderToolbarItem(addNewRecordActionButton);

        Button sendActionButton = new Button("Send", new Command() {
            @Override
            public void execute() {
                ((DirectDebitSimFileViewerView.Presenter) getPresenter()).send();
            }

        });
        addHeaderToolbarItem(sendActionButton);
    }

    @Override
    public void reportSendResult(boolean hasFailed, String failureMessage) {
        if (hasFailed) {
            MessageDialog.error("Send", failureMessage);
        } else {
            MessageDialog.info("File has been sent successfully");
        }
    }
}
