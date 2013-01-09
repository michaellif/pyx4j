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
package com.propertyvista.admin.client.ui.crud.pmc;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.admin.rpc.EquifaxSetupRequestDTO;

public class EquifaxApprovalViewImpl extends AdminViewerViewImplBase<EquifaxSetupRequestDTO> implements EquifaxApprovalView {

    private static I18n i18n = I18n.get(EquifaxApprovalView.class);

    public EquifaxApprovalViewImpl() {
        super(AdminSiteMap.Management.EquifaxApproval.class);
        setForm(new EquifaxApprovalForm(this));

        Button approveAndSendToEquifax = new Button(i18n.tr("Approve and send to Equifax"));
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

        Button reject = new Button(i18n.tr(i18n.tr("Reject")));
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
}
