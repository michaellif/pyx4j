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
 */
package com.propertyvista.crm.client.ui.gadgets.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

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
        FormPanel formPanel = new FormPanel(this);

        if (!VistaFeatures.instance().yardiIntegration()) {
            formPanel.h2(i18n.tr("Occupancy:"));
            formPanel.append(Location.Left, proto().occupiedUnits(), new CLabel<Integer>()).decorate().componentWidth(180);
            ((CLabel<Integer>) get(proto().occupiedUnits())).setFormatter(new IFormatter<Integer, String>() {
                @Override
                public String format(Integer value) {
                    int total = getValue().totalUnits().getValue();
                    int occupied = value;
                    double percent = total != 0 ? occupied / (double) total : 0;
                    return i18n.tr("{0} of {1} ({2,number,percent})", value, getValue().totalUnits().getValue(), percent);
                }
            });
        }

        formPanel.h2(i18n.tr("Leases on Month to Month:"));
        formPanel.append(Location.Left, proto().numOfLeasesOnMonthToMonth()).decorate().customLabel("").useLabelSemicolon(false).componentWidth(50);

        formPanel.h2(i18n.tr("Leases Ending:"));
        formPanel.append(Location.Left, proto().numOfLeasesEndingThisMonth()).decorate().customLabel(i18n.tr("This Month")).componentWidth(50);
        formPanel.append(Location.Left, proto().numOfLeasesEndingNextMonth()).decorate().customLabel(i18n.tr("Next Month")).componentWidth(50);
        formPanel.append(Location.Left, proto().numOfLeasesEnding60to90Days()).decorate().customLabel(i18n.tr("60 to 90 Days")).componentWidth(50);
        formPanel.append(Location.Left, proto().numOfLeasesEndingOver90Days()).decorate().customLabel(i18n.tr("90+ Days")).componentWidth(50);
        return formPanel;

    }
}