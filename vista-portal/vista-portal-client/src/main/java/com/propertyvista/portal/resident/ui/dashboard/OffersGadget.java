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
package com.propertyvista.portal.resident.ui.dashboard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;

public class OffersGadget extends AbstractGadget<MainDashboardViewImpl> {

    private static final I18n i18n = I18n.get(OffersGadget.class);

    OffersGadget(MainDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.offersIcon(), i18n.tr("My Perks & Offers"), ThemeColor.contrast6, 1);
        setActionsToolbar(new ResidentServicesToolbar());
        FlowPanel contentPanel = new FlowPanel();
        contentPanel.add(new HTML("Great news! As a resident , we've got some great local offers and deals just for you."));

        setContent(contentPanel);
    }

//    protected void populate(ResidentServicesDashboardDTO value) {
//
//    }

    class ResidentServicesToolbar extends Toolbar {
        public ResidentServicesToolbar() {

            Button offersButton = new Button("View my Perks & Offers", new Command() {

                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Offers());
                }
            });
            offersButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast6, 1));
            addItem(offersButton);

        }
    }

}
