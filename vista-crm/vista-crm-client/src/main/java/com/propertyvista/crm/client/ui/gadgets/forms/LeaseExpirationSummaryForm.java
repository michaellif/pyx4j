/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.forms;

import java.text.ParseException;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;
import com.propertyvista.shared.config.VistaFeatures;

public final class LeaseExpirationSummaryForm extends ZoomableViewForm<LeaseExpirationGadgetDataDTO> {

    private static final I18n i18n = I18n.get(LeaseExpirationSummaryForm.class);

    public LeaseExpirationSummaryForm() {
        super(LeaseExpirationGadgetDataDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;
        if (!VistaFeatures.instance().yardiIntegration()) {
            content.setH2(++row, 0, 1, i18n.tr("Occupancy:"));

            content.setWidget(++row, 0, inject(proto().occupiedUnits(), new CLabel<Integer>(), new FieldDecoratorBuilder().componentWidth(15).build()));
            ((CLabel<Integer>) get(proto().occupiedUnits())).setFormatter(new IFormatter<Integer>() {

                @Override
                public String format(Integer value) {
                    int total = getValue().totalUnits().getValue();
                    int occupied = value;
                    double percent = total != 0 ? occupied / (double) total : 0;
                    return i18n.tr("{0} of {1} ({2,number,percent})", value, getValue().totalUnits().getValue(), percent);
                }
            });
        }

        content.setH2(++row, 0, 1, i18n.tr("Leases on Month to Month:"));
        content.setWidget(++row, 0,
                inject(proto().numOfLeasesOnMonthToMonth(), new FieldDecoratorBuilder().customLabel("").componentWidth(5).useLabelSemicolon(false).build()));

        content.setH2(++row, 0, 1, i18n.tr("Leases Ending:"));
        content.setWidget(++row, 0,
                inject(proto().numOfLeasesEndingThisMonth(), new FieldDecoratorBuilder().customLabel(i18n.tr("This Month")).componentWidth(5).build()));
        content.setWidget(++row, 0,
                inject(proto().numOfLeasesEndingNextMonth(), new FieldDecoratorBuilder().customLabel(i18n.tr("Next Month")).componentWidth(5).build()));
        content.setWidget(++row, 0,
                inject(proto().numOfLeasesEnding60to90Days(), new FieldDecoratorBuilder().customLabel(i18n.tr("60 to 90 Days")).componentWidth(5).build()));
        content.setWidget(++row, 0,
                inject(proto().numOfLeasesEndingOver90Days(), new FieldDecoratorBuilder().customLabel(i18n.tr("90+ Days")).componentWidth(5).build()));

        return content;

    }
}