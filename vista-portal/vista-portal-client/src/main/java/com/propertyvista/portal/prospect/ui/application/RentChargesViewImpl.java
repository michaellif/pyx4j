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
 */
package com.propertyvista.portal.prospect.ui.application;

import com.google.gwt.user.client.ui.FlowPanel;

import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class RentChargesViewImpl extends FlowPanel implements RentChargesView {

    private final RentChargesSummaryGadget chargesSummaryGadget;

    public RentChargesViewImpl() {

        setStyleName(PortalRootPaneTheme.StyleName.ExtraGadget.name());

        chargesSummaryGadget = new RentChargesSummaryGadget();
        add(chargesSummaryGadget);

    }

    @Override
    public void populate(OnlineApplicationDTO onlineApplication) {
        chargesSummaryGadget.populate(onlineApplication);
    }
}
