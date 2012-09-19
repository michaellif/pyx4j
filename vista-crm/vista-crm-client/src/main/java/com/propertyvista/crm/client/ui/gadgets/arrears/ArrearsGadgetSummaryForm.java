/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetSummaryForm;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;

public class ArrearsGadgetSummaryForm extends CounterGadgetSummaryForm<ArrearsGadgetDataDTO> {

    private static final I18n i18n = I18n.get(ArrearsGadgetSummaryForm.class);

    public ArrearsGadgetSummaryForm() {
        super(ArrearsGadgetDataDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setH2(++row, 0, 1, i18n.tr("Delinquent Tenants:"));

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().delinquentTenants())).customLabel("").useLabelSemicolon(false).build());

        content.setH2(++row, 0, 1, i18n.tr("Outstanding"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstandingThisMonth())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstanding1to30Days())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstanding31to60Days())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstanding61to90Days())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstanding91andMoreDays())).build());

        content.setH3(++row, 0, 1, i18n.tr("Total"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstandingTotal())).customLabel("").useLabelSemicolon(false).build());

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().outstandingThisMonth()).setValue(i18n.tr("${0}", getValue().bucketThisMonth().getValue()));
        get(proto().outstanding1to30Days()).setValue(i18n.tr("${0}", getValue().buckets().bucket30().getValue()));
        get(proto().outstanding31to60Days()).setValue(i18n.tr("${0}", getValue().buckets().bucket60().getValue()));
        get(proto().outstanding61to90Days()).setValue(i18n.tr("${0}", getValue().buckets().bucket90().getValue()));
        get(proto().outstanding91andMoreDays()).setValue(i18n.tr("${0}", getValue().buckets().bucketOver90().getValue()));
        get(proto().outstandingTotal()).setValue(i18n.tr("${0}", getValue().buckets().arrearsAmount().getValue()));
    }

}
