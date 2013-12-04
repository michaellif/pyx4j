/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui;

import com.google.gwt.user.client.ui.FlowPanel;

import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class RentalSummaryViewImpl extends FlowPanel implements RentalSummaryView {

    private final RentalSummaryGadget rentalSummaryGadget;

    private final ChargesSummaryGadget chargesSummaryGadget;

    public RentalSummaryViewImpl() {

        setStyleName(PortalRootPaneTheme.StyleName.ExtraGadget.name());

        rentalSummaryGadget = new RentalSummaryGadget();
        add(rentalSummaryGadget);

        chargesSummaryGadget = new ChargesSummaryGadget();
        add(chargesSummaryGadget);

    }

    @Override
    public void populate(OnlineApplicationDTO onlineApplication) {
        rentalSummaryGadget.populate(onlineApplication);
        chargesSummaryGadget.populate(onlineApplication);
    }
}
