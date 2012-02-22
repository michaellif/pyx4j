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

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.tenant.lease.bill.BillLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerViewImpl extends CrmViewerViewImplBase<LeaseDTO> implements LeaseViewerView {

    private static final I18n i18n = I18n.get(LeaseViewerViewImpl.class);

    private final IListerView<Bill> billLister;

    private final Button createApplicationAction;

    private final Button runBillAction;

    private final Button notice;

    private final Button cancelNotice;

    private final Button evict;

    private final Button cancelEvict;

    public LeaseViewerViewImpl() {
        super(CrmSiteMap.Tenants.Lease.class);

        billLister = new ListerInternalViewImplBase<Bill>(new BillLister());

        //set main form here:
        setForm(new LeaseEditorForm(true));

        createApplicationAction = new Button(i18n.tr("Create Application"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).createMasterApplication();
            }
        });
        addToolbarItem(createApplicationAction.asWidget());

        runBillAction = new Button(i18n.tr("Run Bill"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).startBilling();
            }
        });
        addToolbarItem(runBillAction.asWidget());

        notice = new Button(i18n.tr("Notice"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).notice();
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

        evict = new Button(i18n.tr("Evict"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).evict();
            }
        });
        addToolbarItem(notice.asWidget());

        cancelEvict = new Button(i18n.tr("Cancel Evict"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView.Presenter) presenter).cancelEvict();
            }
        });
        addToolbarItem(cancelNotice.asWidget());
    }

    @Override
    public void populate(LeaseDTO value) {
        super.populate(value);

        // set buttons state:
        Status status = value.status().getValue();
        createApplicationAction.setVisible(status == Status.Draft);
        runBillAction.setVisible(status == Status.Active);
        notice.setVisible(status == Status.Active);
        cancelNotice.setVisible(status == Status.OnNotice);
        evict.setVisible(status == Status.Active);
        cancelEvict.setVisible(status == Status.Broken);
    }

    @Override
    public IListerView<Bill> getBillListerView() {
        return billLister;
    }
}