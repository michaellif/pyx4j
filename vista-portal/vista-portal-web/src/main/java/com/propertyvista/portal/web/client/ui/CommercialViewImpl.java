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
package com.propertyvista.portal.web.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.propertyvista.common.client.site.Commercial;
import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class CommercialViewImpl extends FlowPanel implements CommercialView {

    private final FlowPanel contentPanel;

    public CommercialViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.Commercial.name());

        contentPanel = new FlowPanel();

        add(contentPanel);

    }

    @Override
    public void populate(List<Commercial> commercials) {
        contentPanel.clear();
        if (commercials.size() == 0) {
            setVisible(false);
        } else {
            setVisible(true);
            for (final Commercial commercial : commercials) {

                FlowPanel message = new FlowPanel();
                message.setStyleName(PortalWebRootPaneTheme.StyleName.CommercialItem.name());

                HTML title = new HTML(commercial.getTitle());
                title.setStyleName(PortalWebRootPaneTheme.StyleName.CommercialItemTitle.name());

                HTML body = new HTML(commercial.getMessage());

                message.add(title);
                message.add(body);

                contentPanel.add(message);

            }

        }

    }

}
