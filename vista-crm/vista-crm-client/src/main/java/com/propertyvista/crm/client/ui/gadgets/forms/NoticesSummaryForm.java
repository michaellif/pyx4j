/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.NoticesGadgetDataDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class NoticesSummaryForm extends ZoomableViewForm<NoticesGadgetDataDTO> {

    private static final I18n i18n = I18n.get(NoticesSummaryForm.class);

    public NoticesSummaryForm() {
        super(NoticesGadgetDataDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        if (!VistaFeatures.instance().yardiIntegration()) {
            formPanel.h2(i18n.tr("Vacancy:"));
            formPanel.append(Location.Left, proto().vacantUnits(), new CLabel<Integer>()).decorate().customLabel(i18n.tr("Units Vacant")).componentWidth(150);
            ((CLabel<Integer>) get(proto().vacantUnits())).setFormatter(new IFormatter<Integer, String>() {
                @Override
                public String format(Integer value) {
                    int vacant = getValue().vacantUnits().getValue();
                    int total = getValue().totalUnits().getValue();
                    double percent = total != 0 ? vacant / (double) total : 0d;
                    return i18n.tr("{0} of {1} ({2,number,percent})", vacant, total, percent);
                }
            });
        }
        formPanel.h2(i18n.tr("Notices Leaving:"));
        formPanel.append(Location.Left, proto().noticesLeavingThisMonth()).decorate().customLabel(i18n.tr("This Month")).componentWidth(50);
        formPanel.append(Location.Left, proto().noticesLeavingNextMonth()).decorate().customLabel(i18n.tr("Next Month")).componentWidth(50);
        formPanel.append(Location.Left, proto().noticesLeaving60to90Days()).decorate().customLabel(i18n.tr("60 to 90 Days")).componentWidth(50);
        formPanel.append(Location.Left, proto().noticesLeavingOver90Days()).decorate().customLabel(i18n.tr("90+")).componentWidth(50);
        return formPanel;

    }

}
