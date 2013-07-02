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

import com.propertyvista.common.client.site.ExtraGadget;
import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class ExtraViewImpl extends FlowPanel implements ExtraView {

    private final FlowPanel contentPanel;

    public ExtraViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.ExtraGadget.name());

        contentPanel = new FlowPanel();

        add(contentPanel);

    }

    @Override
    public void populate(List<ExtraGadget> gadgets) {
        contentPanel.clear();
        if (gadgets.size() == 0) {
            setVisible(false);
        } else {
            setVisible(true);
            for (final ExtraGadget gadget : gadgets) {

                FlowPanel message = new FlowPanel();
                message.setStyleName(PortalWebRootPaneTheme.StyleName.ExtraGadgetItem.name());

                HTML title = new HTML(gadget.getTitle());
                title.setStyleName(PortalWebRootPaneTheme.StyleName.ExtraGadgetItemTitle.name());

                HTML body = new HTML(gadget.getMessage());

                message.add(title);
                message.add(body);

                contentPanel.add(message);

            }

        }

    }

}
