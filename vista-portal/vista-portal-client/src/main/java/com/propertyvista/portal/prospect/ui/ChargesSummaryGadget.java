/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.prospect.dto.RentalSummaryDTO;
import com.propertyvista.portal.shared.themes.BlockMixin;
import com.propertyvista.portal.shared.themes.ExtraGadgetsTheme;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class ChargesSummaryGadget extends FlowPanel {

    private static final I18n i18n = I18n.get(ChargesSummaryGadget.class);

    public ChargesSummaryGadget() {
        super();

        setStyleName(PortalRootPaneTheme.StyleName.ExtraGadgetItem.name());
        addStyleName(BlockMixin.StyleName.PortalBlock.name());

        HTML titleHTML = new HTML(i18n.tr("Charges"));
        titleHTML.setStyleName(PortalRootPaneTheme.StyleName.ExtraGadgetItemTitle.name());

        add(titleHTML);

        FlowPanel panel = new FlowPanel();

        HTML applicationChargesHTML = new HTML(i18n.tr("Application Charges"));
        applicationChargesHTML.setStyleName(ExtraGadgetsTheme.StyleName.CommunityEventCaption.name());
        panel.add(applicationChargesHTML);

        HTML monthlyChargesHTML = new HTML(i18n.tr("Monthly Charges"));
        monthlyChargesHTML.setStyleName(ExtraGadgetsTheme.StyleName.CommunityEventCaption.name());
        panel.add(monthlyChargesHTML);

        add(panel);
    }

    public void populate(RentalSummaryDTO rentalSummary) {
        // TODO Auto-generated method stub

    }

}
