/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.financial.dashboard;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.web.dto.LatestActivitiesDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;

public class LatestActivitiesGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(LatestActivitiesGadget.class);

    LatestActivitiesGadget(FinancialDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.billingIcon(), i18n.tr("Latest Activities"), ThemeColor.contrast4);
        FlowPanel contentPanel = new FlowPanel();
        contentPanel.add(new HTML("Latest Activities"));

        setContent(contentPanel);
    }

    protected void populate(LatestActivitiesDTO value) {

    }

}
