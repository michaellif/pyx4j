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
package com.propertyvista.portal.resident.ui.extra;

import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;

import com.propertyvista.portal.resident.ui.extra.events.CommunityEventsGadget;
import com.propertyvista.portal.resident.ui.extra.weather.WeatherGadget;
import com.propertyvista.portal.rpc.portal.resident.dto.CommunityEventsGadgetDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.ExtraGadgetDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.WeatherGadgetDTO;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class ExtraViewImpl extends FlowPanel implements ExtraView {

    private final FlowPanel contentPanel;

    public ExtraViewImpl() {

        setStyleName(PortalRootPaneTheme.StyleName.ExtraGadget.name());

        contentPanel = new FlowPanel();

        add(contentPanel);

    }

    @Override
    public void populate(List<ExtraGadgetDTO> gadgets) {
        contentPanel.clear();
        if (gadgets.size() == 0) {
            setVisible(false);
        } else {
            setVisible(true);
            for (final ExtraGadgetDTO gadget : gadgets) {
                if (gadget.isInstanceOf(WeatherGadgetDTO.class)) {
                    contentPanel.add(new WeatherGadget((WeatherGadgetDTO) gadget));
                } else if (gadget.isInstanceOf(CommunityEventsGadgetDTO.class)) {
                    contentPanel.add(new CommunityEventsGadget((CommunityEventsGadgetDTO) gadget));
                }

            }

        }

    }
}
