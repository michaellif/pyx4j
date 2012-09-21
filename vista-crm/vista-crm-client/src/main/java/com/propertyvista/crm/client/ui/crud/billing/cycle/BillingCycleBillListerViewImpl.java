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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.cycle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.components.boxes.ReasonBox;
import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;

public class BillingCycleBillListerViewImpl extends CrmListerViewImplBase<BillDataDTO> implements BillingCycleBillListerView {

    private final static I18n i18n = I18n.get(BillingCycleBillListerViewImpl.class);

    private final Button approveAction;

    private final Button rejectAction;

    private final Button print;

    public BillingCycleBillListerViewImpl() {
        super(CrmSiteMap.Finance.Bill.class);
        setLister(new BillingCycleBillLister());

        // Add actions:

        approveAction = new Button(i18n.tr("Confirm Selected"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!getLister().getDataTablePanel().getDataTable().getCheckedItems().isEmpty()) {
                    ((BillingCycleBillListerView.Presenter) getPresenter()).confirm(getLister().getDataTablePanel().getDataTable().getCheckedItems());
                }
            }
        });
        addHeaderToolbarItem(approveAction.asWidget());

        rejectAction = new Button(i18n.tr("Reject Selected"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!getLister().getDataTablePanel().getDataTable().getCheckedItems().isEmpty()) {
                    new ReasonBox(i18n.tr("Reject Selected")) {
                        @Override
                        public boolean onClickOk() {
                            ((BillingCycleBillListerView.Presenter) getPresenter()).reject(getLister().getDataTablePanel().getDataTable().getCheckedItems(),
                                    getReason());
                            return true;
                        }
                    }.show();
                }
            }
        });
        addHeaderToolbarItem(rejectAction.asWidget());

        print = new Button(i18n.tr("Print Selected"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!getLister().getDataTablePanel().getDataTable().getCheckedItems().isEmpty()) {
                    ((BillingCycleBillListerView.Presenter) getPresenter()).print(getLister().getDataTablePanel().getDataTable().getCheckedItems());
                }
            }
        });
        addHeaderToolbarItem(print.asWidget());

    }

    @Override
    public void setActionButtonsVisible(boolean visible) {
        approveAction.setVisible(visible);
        rejectAction.setVisible(visible);
    }
}
