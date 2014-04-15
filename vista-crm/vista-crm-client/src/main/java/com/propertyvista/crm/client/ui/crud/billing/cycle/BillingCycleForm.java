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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.domain.financial.billing.Bill;

class BillingCycleForm extends CrmEntityForm<BillingCycleDTO> {

    private static final I18n i18n = I18n.get(BillingCycleForm.class);

    public BillingCycleForm(IForm<BillingCycleDTO> view) {
        super(BillingCycleDTO.class, view);
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, injectAndDecorate(proto().billingType()));
        content.setWidget(++row, 0, injectAndDecorate(proto().billingCycleStartDate()));
        content.setWidget(++row, 0, injectAndDecorate(proto().billingCycleEndDate()));
        content.setWidget(++row, 0, injectAndDecorate(proto().targetBillExecutionDate()));

        content.setH2(++row, 0, 2, i18n.tr("Statistics"));
        content.setWidget(++row, 0, injectAndDecorate(proto().stats().failed()));
        content.setWidget(row, 1, new ViewBillsLink(Bill.BillStatus.Failed));

        content.setWidget(++row, 0, injectAndDecorate(proto().stats().rejected()));
        content.setWidget(row, 1, new ViewBillsLink(Bill.BillStatus.Rejected));

        content.setWidget(++row, 0, injectAndDecorate(proto().stats().notConfirmed()));
        content.setWidget(row, 1, new ViewBillsLink(Bill.BillStatus.Finished));

        content.setWidget(++row, 0, injectAndDecorate(proto().stats().confirmed()));
        content.setWidget(row, 1, new ViewBillsLink(Bill.BillStatus.Confirmed));

        content.setBR(++row, 0, 2);
        content.setWidget(++row, 0, injectAndDecorate(proto().total()));
        content.setWidget(row, 1, new ViewLeasesLink(false));
        content.setWidget(++row, 0, injectAndDecorate(proto().notRun()));
        content.setWidget(row, 1, new ViewLeasesLink(true));

        content.setH2(++row, 0, 2, i18n.tr("AutoPay"));
        content.setWidget(++row, 0, injectAndDecorate(proto().actualAutopayExecutionDate()));
        content.setWidget(++row, 0, injectAndDecorate(proto().targetAutopayExecutionDate()));
        content.setWidget(++row, 0, injectAndDecorate(proto().pads()));
        content.setWidget(row, 1, new ViewPadLink());

        setTabBarVisible(false);
        selectTab(addTab(content));
    }

    // builder specifically for this form (enlarge default label width)
    protected class DecoratorBuilder extends WidgetDecorator.Builder {

        public DecoratorBuilder() {
            super();
            labelWidth(20);
            componentWidth(15);
        }
    }

    private class ViewBillsLink extends Anchor {
        public ViewBillsLink(final Bill.BillStatus billStatusValue) {
            super(i18n.tr("View"));

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
