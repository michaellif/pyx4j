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
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.tenant.lease.bill.BillLister;
import com.propertyvista.crm.client.ui.crud.tenant.lease.payment.PaymentLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Payment;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerViewImpl extends CrmViewerViewImplBase<LeaseDTO> implements LeaseViewerView {

    private static final I18n i18n = I18n.get(LeaseViewerViewImpl.class);

    private final IListerView<Bill> billLister;

    private final IListerView<Payment> paymentLister;

    private final Button createApplication;

    private final Button runBill;

    private final Button notice;

    private final Button cancelNotice;

    private final Button evict;

    private final Button cancelEvict;

    public LeaseViewerViewImpl() {
        super(CrmSiteMap.Tenants.Lease.class);

        billLister = new ListerInternalViewImplBase<Bill>(new BillLister());

        paymentLister = new ListerInternalViewImplBase<Payment>(new PaymentLister());

        //set main form here:
        setForm(new LeaseEditorForm(true));

        createApplication = new Button(i18n.tr("Create Application"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).createMasterApplication();
            }
        });
        addToolbarItem(createApplication.asWidget());

        runBill = new Button(i18n.tr("Run Bill"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).startBilling();
            }
        });
        addToolbarItem(runBill.asWidget());

        notice = new Button(i18n.tr("Notice..."), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new ActionBox(CompletionType.Notice) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) presenter).notice(getDate(), getMoveOutDate());
                        return true;
                    }
                }.show();
            }
        });
        addToolbarItem(notice.asWidget());

        cancelNotice = new Button(i18n.tr("Cancel Notice"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).cancelNotice();
            }
        });
        addToolbarItem(cancelNotice.asWidget());

        evict = new Button(i18n.tr("Evict..."), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new ActionBox(CompletionType.Eviction) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) presenter).evict(getDate(), getMoveOutDate());
                        return true;
                    }
                }.show();
            }
        });
        addToolbarItem(evict.asWidget());

        cancelEvict = new Button(i18n.tr("Cancel Evict"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).cancelEvict();
            }
        });
        addToolbarItem(cancelEvict.asWidget());
    }

    @Override
    public void populate(LeaseDTO value) {
        super.populate(value);

        // set buttons state:
        Status status = value.status().getValue();
        CompletionType completion = value.completion().getValue();

        createApplication.setVisible(status == Status.Draft);

        runBill.setVisible(status == Status.Active);

        notice.setVisible(status == Status.Active && completion == CompletionType.None);
        cancelNotice.setVisible(completion == CompletionType.Notice);
        evict.setVisible(status == Status.Active && completion == CompletionType.None);
        cancelEvict.setVisible(completion == CompletionType.Eviction);
    }

    @Override
    public IListerView<Bill> getBillListerView() {
        return billLister;
    }

    @Override
    public IListerView<Payment> getPaymentListerView() {
        return paymentLister;
    }

    private abstract class ActionBox extends OkCancelDialog {

        private final CDatePicker date = new CDatePicker();

        private final CDatePicker moveOut = new CDatePicker();

        private final CompletionType action;

        public ActionBox(CompletionType action) {
            super(i18n.tr("Please select"));
            this.action = action;
            setBody(createBody());
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            VerticalPanel content = new VerticalPanel();
            Widget label = null;

            HorizontalPanel datePanel = new HorizontalPanel();
            switch (action) {
            case Notice:
                datePanel.add(label = new HTML(i18n.tr("Notice Date") + ":"));
                break;
            case Eviction:
                datePanel.add(label = new HTML(i18n.tr("Evict Date") + ":"));
                break;
            }
            datePanel.setCellWidth(label, "100px");
            datePanel.setSpacing(4);
            datePanel.add(date);
            date.setWidth("9em");
            date.setValue(new LogicalDate());

            content.add(datePanel);

            datePanel = new HorizontalPanel();
            datePanel.add(label = new HTML(i18n.tr("Move Out Date") + ":"));
            datePanel.setCellWidth(label, "100px");
            datePanel.setSpacing(4);
            datePanel.add(moveOut);
            moveOut.setWidth("9em");
            moveOut.setValue(new LogicalDate());

            content.add(datePanel);

            content.setWidth("100%");
            return content.asWidget();
        }

        public LogicalDate getDate() {
            return new LogicalDate(date.getValue());
        }

        public LogicalDate getMoveOutDate() {
            return new LogicalDate(moveOut.getValue());
        }
    }
}