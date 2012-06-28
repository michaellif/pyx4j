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

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.domain.financial.billing.Bill;

class BillingCycleForm extends CrmEntityForm<BillingCycleDTO> {

    private static final I18n i18n = I18n.get(BillingCycleForm.class);

    public BillingCycleForm() {
        super(BillingCycleDTO.class, true);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingType())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycleStartDate())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycleEndDate())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionTargetDate())).build());

        content.setH2(++row, 0, 2, i18n.tr("Statistics"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().failed())).build());
        content.setWidget(row, 1, new ViewLink(Bill.BillStatus.Failed));

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().rejected())).build());
        content.setWidget(row, 1, new ViewLink(Bill.BillStatus.Rejected));

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().notConfirmed())).build());
        content.setWidget(row, 1, new ViewLink(Bill.BillStatus.Finished));

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().confirmed())).build());
        content.setWidget(row, 1, new ViewLink(Bill.BillStatus.Confirmed));

        content.setBR(++row, 0, 2);
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().total())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().notRun())).build());

        content.getColumnFormatter().setWidth(0, "40%");
        content.getColumnFormatter().setWidth(1, "60%");

        selectTab(addTab(content, i18n.tr("General")));
    }

    // builder specifically for this form (enlarge default label width)
    protected class DecoratorBuilder extends WidgetDecorator.Builder {

        public DecoratorBuilder(CComponent<?, ?> component) {
            super(component);
            readOnlyMode(!isEditable());
            labelWidth(20);
            componentWidth(15);
        }
    }

    private class ViewLink extends Anchor {
        public ViewLink(final Bill.BillStatus billStatusValue) {
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
}
