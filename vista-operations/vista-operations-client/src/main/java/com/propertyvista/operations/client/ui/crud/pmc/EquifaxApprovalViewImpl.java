/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog.Type;
import com.pyx4j.widgets.client.dialog.OkOption;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.rpc.dto.EquifaxSetupRequestDTO;

public class EquifaxApprovalViewImpl extends OperationsViewerViewImplBase<EquifaxSetupRequestDTO> implements EquifaxApprovalView {

    private static I18n i18n = I18n.get(EquifaxApprovalView.class);

    private final Button approveAndSendToEquifax;

    private final Button reject;

    public EquifaxApprovalViewImpl() {
        getEditButton().setVisible(false);
        setForm(new EquifaxApprovalForm(this));

        approveAndSendToEquifax = new Button(i18n.tr("Approve and send to Equifax"));
        approveAndSendToEquifax.setCommand(new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Are you sure?"), i18n.tr("Send the setup form to equifax"), new Command() {
                    @Override
                    public void execute() {
                        ((EquifaxApprovalView.Presenter) getPresenter()).approveAndSendToEquifax();
                    }
                });
            }
        });
        addHeaderToolbarItem(approveAndSendToEquifax);

        reject = new Button(i18n.tr(i18n.tr("Reject")));
        reject.setCommand(new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Are you sure?"), i18n.tr("Reject the setup request?"), new Command() {
                    @Override
                    public void execute() {
                        ((EquifaxApprovalView.Presenter) getPresenter()).reject();
                    }
                });
            }
        });
        addHeaderToolbarItem(reject);
    }

    @Override
    public void setEnableApprovalControls(boolean isApprovalControlsEnabled) {
        approveAndSendToEquifax.setEnabled(isApprovalControlsEnabled);
        reject.setEnabled(isApprovalControlsEnabled);
    }

    @Override
    public void reportResult(String result) {
        MessageDialog.show(i18n.tr(""), result, Type.Info, new OkOption() {

            @Override
            public boolean onClickOk() {
                ((EquifaxApprovalView.Presenter) getPresenter()).confirmSuccess();
                return true;
            }

        });

    }
}
