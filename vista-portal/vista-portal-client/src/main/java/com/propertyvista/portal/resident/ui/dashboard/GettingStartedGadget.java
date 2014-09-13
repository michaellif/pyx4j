/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 2, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.dashboard;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.shared.config.VistaFeatures;

public class GettingStartedGadget extends AbstractGadget<MainDashboardViewImpl> {

    private static final I18n i18n = I18n.get(GettingStartedGadget.class);

    GettingStartedGadget(MainDashboardViewImpl form) {
        super(form, null, i18n.tr("Getting Started using myCommunity Resident Portal"), ThemeColor.contrast1, 1);
        asWidget().setStyleName(DashboardTheme.StyleName.Gadget.name());

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.LEFT);

        StringBuilder content = new StringBuilder();

        content.append(i18n.tr("Now you are ready to start using myCommunity Resident Portal on a regular basis. It's really up to you what you do next."));

        content.append("<p/><ul><li>");
        content.append(i18n.tr("Update your Profile or Account."));
        content.append("</li><li>");
        content.append(i18n.tr("Communicate to your Property Management Office."));
        content.append("</li><li>");
        content.append(i18n.tr("Make a payment and see your billing history."));
        content.append("</li><li>");
        content.append(i18n.tr("Setup Auto Pay."));
        content.append("</li><li>");
        content.append(i18n.tr("Send a Maintanance Request."));
        content.append("</li><li>");
        content.append(i18n.tr("Purchase Insurance."));
        content.append("</li></ul>");

        contentPanel.add(new HTML(content.toString()));

        setContent(contentPanel);

        setNavigationBar(new NavigationBar());

    }

    class NavigationBar extends FlowPanel {
        public NavigationBar() {
            if (!VistaFeatures.instance().yardiIntegration()) {

                Anchor anchor = new Anchor(i18n.tr("Hide Getting Started"), new Command() {

                    @Override
                    public void execute() {

                    }
                });
                anchor.setTitle(i18n.tr("You can use setting menu to show Getting Started again."));
                add(anchor);

            }
        }
    }

}
