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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.lease.bill;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.dto.BillDTO;

public class BillViewerViewImpl extends CrmViewerViewImplBase<BillDTO> implements BillViewerView {

    private final static I18n i18n = I18n.get(BillViewerViewImpl.class);

    private static final String APPROVE = i18n.tr("Confirm");

    private static final String DECLINE = i18n.tr("Reject");

    private static final String PRINT = i18n.tr("Print");

    private final Button approveAction;

    private final Button declineAction;

    private final Button print;

    public BillViewerViewImpl() {
        super(CrmSiteMap.Tenants.Bill.class, new BillEditorForm(true), true);

        // Add actions:

        print = new Button(PRINT, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((BillViewerView.Presenter) presenter).print();
            }
        });
        addToolbarItem(print.asWidget());

        approveAction = new Button(APPROVE, new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((BillViewerView.Presenter) presenter).confirm();
            }
        });
        addToolbarItem(approveAction.asWidget());

        declineAction = new Button(DECLINE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ActionBox(DECLINE) {
                    @Override
                    public boolean onClickOk() {
                        ((BillViewerView.Presenter) presenter).reject(getReason());
                        return true;
                    }
                }.show();
            }
        });
        addToolbarItem(declineAction.asWidget());

    }

    @Override
    public void populate(BillDTO value) {
        approveAction.setVisible(value.billStatus().getValue() == BillStatus.Finished);
        declineAction.setVisible(value.billStatus().getValue() == BillStatus.Finished);
        super.populate(value);
    }

    private abstract class ActionBox extends OkCancelDialog {

        private final CTextArea reason = new CTextArea();

        public ActionBox(String title) {
            super(title);
            setBody(createBody());
            setSize("350px", "100px");
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            VerticalPanel content = new VerticalPanel();
            content.add(new HTML(i18n.tr("Please fill the reason") + ":"));
            content.add(reason);

            reason.setWidth("100%");
            content.setWidth("100%");
            return content.asWidget();
        }

        public String getReason() {
            return reason.getValue();
        }
    }
}