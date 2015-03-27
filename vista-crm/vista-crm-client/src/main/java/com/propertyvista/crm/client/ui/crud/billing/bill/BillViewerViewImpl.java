/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.billing.bill;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.widgets.client.Button.SecureMenuItem;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.components.boxes.ReasonBox;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseConfirmBill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;

public class BillViewerViewImpl extends CrmViewerViewImplBase<BillDataDTO> implements BillViewerView {

    private final static I18n i18n = I18n.get(BillViewerViewImpl.class);

    private static final String APPROVE = i18n.tr("Confirm");

    private static final String DECLINE = i18n.tr("Reject");

    private static final String PRINT = i18n.tr("Print");

    private final MenuItem approveAction;

    private final MenuItem rejectAction;

    private final MenuItem printAction;

    public BillViewerViewImpl() {
        super(true);
        setForm(new BillDataForm(this, false));
        // Add actions:

        printAction = new SecureMenuItem(PRINT, new Command() {
            @Override
            public void execute() {
                ((BillViewerView.Presenter) getPresenter()).print();
            }
        }, DataModelPermission.permissionUpdate(BillDataDTO.class));
        addAction(printAction);

        approveAction = new SecureMenuItem(APPROVE, new Command() {
            @Override
            public void execute() {
                ((BillViewerView.Presenter) getPresenter()).confirm();
            }
        }, new ActionPermission(LeaseConfirmBill.class));
        addAction(approveAction);

        rejectAction = new SecureMenuItem(DECLINE, new Command() {
            @Override
            public void execute() {
                new ReasonBox(DECLINE) {
                    @Override
                    public boolean onClickOk() {
                        if (CommonsStringUtils.isEmpty(getReason())) {
                            MessageDialog.error(i18n.tr("Error"), i18n.tr("Please fill the reason"));
                            return false;
                        }
                        ((BillViewerView.Presenter) getPresenter()).reject(getReason());
                        return true;
                    }
                }.show();
            }
        }, new ActionPermission(LeaseConfirmBill.class));
        addAction(rejectAction);
    }

    @Override
    public void reset() {
        setActionVisible(approveAction, false);
        setActionVisible(rejectAction, false);
        super.reset();
    }

    @Override
    public void populate(BillDataDTO value) {
        super.populate(value);

        setActionVisible(approveAction, value.bill().billStatus().getValue() == BillStatus.Finished);
        setActionVisible(rejectAction, value.bill().billStatus().getValue() == BillStatus.Finished);
    }
}