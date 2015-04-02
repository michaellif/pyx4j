/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.billing.cycle;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.components.boxes.ReasonBox;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseConfirmBill;

public class BillingCycleBillListerViewImpl extends AbstractListerView<BillDataDTO> implements BillingCycleBillListerView {

    private final static I18n i18n = I18n.get(BillingCycleBillListerViewImpl.class);

    private final Button approveAction;

    private final Button rejectAction;

    private final Button print;

    public BillingCycleBillListerViewImpl() {
        setDataTablePanel(new BillingCycleBillLister());

        // Add actions:

        approveAction = new Button(i18n.tr("Confirm Selected"), new Command() {
            @Override
            public void execute() {
                if (!getDataTablePanel().getDataTable().getSelectedItems().isEmpty()) {
                    ((BillingCycleBillListerView.Presenter) getPresenter()).confirm(getDataTablePanel().getDataTable().getSelectedItems());
                }
            }
        }, new ActionPermission(LeaseConfirmBill.class));
        addHeaderToolbarItem(approveAction.asWidget());

        rejectAction = new Button(i18n.tr("Reject Selected"), new Command() {
            @Override
            public void execute() {
                if (!getDataTablePanel().getDataTable().getSelectedItems().isEmpty()) {
                    new ReasonBox(i18n.tr("Reject Selected")) {
                        @Override
                        public boolean onClickOk() {
                            if (CommonsStringUtils.isEmpty(getReason())) {
                                MessageDialog.error(i18n.tr("Error"), i18n.tr("Please fill the reason"));
                                return false;
                            }
                            ((BillingCycleBillListerView.Presenter) getPresenter()).reject(getDataTablePanel().getDataTable().getSelectedItems(), getReason());
                            return true;
                        }
                    }.show();
                }
            }
        }, new ActionPermission(LeaseConfirmBill.class));
        addHeaderToolbarItem(rejectAction.asWidget());

        print = new Button(i18n.tr("Print Selected"), new Command() {
            @Override
            public void execute() {
                if (!getDataTablePanel().getDataTable().getSelectedItems().isEmpty()) {
                    ((BillingCycleBillListerView.Presenter) getPresenter()).print(getDataTablePanel().getDataTable().getSelectedItems());
                }
            }
        }, DataModelPermission.permissionRead(BillDataDTO.class));
        addHeaderToolbarItem(print.asWidget());
    }

    @Override
    public void setActionButtonsVisible(boolean visible) {
        approveAction.setVisible(visible);
        rejectAction.setVisible(visible);
    }
}
