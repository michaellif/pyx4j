/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.cycle;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.crm.client.themes.ViewLinkTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.domain.financial.billing.Bill;

class BillingCycleForm extends CrmEntityForm<BillingCycleDTO> {

    private static final I18n i18n = I18n.get(BillingCycleForm.class);

    public BillingCycleForm(IForm<BillingCycleDTO> view) {
        super(BillingCycleDTO.class, view);
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().billingType()).decorate();
        formPanel.append(Location.Left, proto().billingCycleStartDate()).decorate();
        formPanel.append(Location.Left, proto().billingCycleEndDate()).decorate();
        formPanel.append(Location.Left, proto().targetBillExecutionDate()).decorate();

        formPanel.h2(i18n.tr("Statistics"));
        formPanel.append(Location.Left, proto().stats().failed()).decorate().assistantWidget(new ViewBillsLink(Bill.BillStatus.Failed));
        formPanel.append(Location.Left, proto().stats().rejected()).decorate().assistantWidget(new ViewBillsLink(Bill.BillStatus.Rejected));
        formPanel.append(Location.Left, proto().stats().notConfirmed()).decorate().assistantWidget(new ViewBillsLink(Bill.BillStatus.Finished));
        formPanel.append(Location.Left, proto().stats().confirmed()).decorate().assistantWidget(new ViewBillsLink(Bill.BillStatus.Confirmed));

        formPanel.br();
        formPanel.append(Location.Left, proto().total()).decorate().assistantWidget(new ViewLeasesLink(false));
        formPanel.append(Location.Left, proto().notRun()).decorate().assistantWidget(new ViewLeasesLink(true));

        formPanel.h2(i18n.tr("AutoPay"));
        formPanel.append(Location.Left, proto().actualAutopayExecutionDate()).decorate();
        formPanel.append(Location.Left, proto().targetAutopayExecutionDate()).decorate();
        formPanel.append(Location.Left, proto().pads()).decorate().assistantWidget(new ViewPadLink());

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("Billing Cycle")));
    }

    private class ViewBillsLink extends Anchor {
        public ViewBillsLink(final Bill.BillStatus billStatusValue) {
            super(i18n.tr("View"));

            addStyleName(ViewLinkTheme.StyleName.ViewLink.name());

            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    AppPlace place = new CrmSiteMap.Finance.BillingCycle.Bills();
                    place.queryArg(CrmSiteMap.Finance.BillingCycle.ARG_BC_ID, getValue().getPrimaryKey().toString());
                    place.queryArg(CrmSiteMap.Finance.BillingCycle.ARG_BILL_STATUS, billStatusValue.name());
                    AppSite.getPlaceController().goTo(place);
                }
            });
        }
    }

    private class ViewLeasesLink extends Anchor {
        public ViewLeasesLink(final Boolean notRun) {
            super(i18n.tr("View"));

            addStyleName(ViewLinkTheme.StyleName.ViewLink.name());

            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    AppPlace place = new CrmSiteMap.Finance.BillingCycle.Leases();
                    if (notRun) {
                        place.queryArg(CrmSiteMap.Finance.BillingCycle.ARG_BC_ID, getValue().getPrimaryKey().toString());
                    }
                    place.queryArg(CrmSiteMap.Finance.BillingCycle.ARG_BT_ID, getValue().billingType().getPrimaryKey().toString());
                    AppSite.getPlaceController().goTo(place);
                }
            });
        }
    }

    private class ViewPadLink extends Anchor {
        public ViewPadLink() {
            super(i18n.tr("View"));

            addStyleName(ViewLinkTheme.StyleName.ViewLink.name());

            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    AppPlace place = new CrmSiteMap.Finance.Payment();
                    place.queryArg(CrmSiteMap.Finance.BillingCycle.ARG_BC_ID, getValue().getPrimaryKey().toString());
                    AppSite.getPlaceController().goTo(place);
                }
            });
        }
    }
}
