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
package com.propertyvista.crm.client.ui.gadgets.forms;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;

public class ArrearsGadgetSummaryForm extends ZoomableViewForm<ArrearsGadgetDataDTO> {

    private static final I18n i18n = I18n.get(ArrearsGadgetSummaryForm.class);

    private final NumberFormat currencyFormat;

    public ArrearsGadgetSummaryForm(NumberFormat currencyFormat) {
        super(ArrearsGadgetDataDTO.class);
        this.currencyFormat = currencyFormat;
    }

    public ArrearsGadgetSummaryForm() {
        this(NumberFormat.getFormat(CMoneyField.symbol + "#,##0.00"));
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setH2(++row, 0, 1, i18n.tr("Delinquent Tenants:"));

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().delinquentTenants())).customLabel("").useLabelSemicolon(false).build());

        content.setH2(++row, 0, 1, i18n.tr("Outstanding:"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstandingThisMonth())).customLabel(i18n.tr("This Month")).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstanding1to30Days())).customLabel(i18n.tr("1 - 30")).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstanding31to60Days())).customLabel(i18n.tr("31 - 60")).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstanding61to90Days())).customLabel(i18n.tr("61 - 90")).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstanding91andMoreDays())).customLabel(i18n.tr("91+")).build());

        content.setH3(++row, 0, 1, i18n.tr("Total:"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstandingTotal())).customLabel("").useLabelSemicolon(false).build());

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().outstandingThisMonth()).setValue(currencyFormat.format(getValue().buckets().bucketThisMonth().getValue()));
        get(proto().outstanding1to30Days()).setValue(currencyFormat.format(getValue().buckets().bucket30().getValue()));
        get(proto().outstanding31to60Days()).setValue(currencyFormat.format(getValue().buckets().bucket60().getValue()));
        get(proto().outstanding61to90Days()).setValue(currencyFormat.format(getValue().buckets().bucket90().getValue()));
        get(proto().outstanding91andMoreDays()).setValue(currencyFormat.format(getValue().buckets().bucketOver90().getValue()));
        get(proto().outstandingTotal()).setValue(currencyFormat.format(getValue().buckets().arrearsAmount().getValue()));
    }

}
