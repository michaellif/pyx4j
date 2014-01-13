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

import com.google.gwt.user.client.ui.FlowPanel;

import com.propertyvista.portal.resident.ui.extra.events.CommunityEventsGadget;
import com.propertyvista.portal.resident.ui.extra.weather.WeatherGadget;
import com.propertyvista.portal.rpc.portal.resident.dto.CommunityEventsGadgetDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.WeatherGadgetDTO;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class ExtraViewImpl extends FlowPanel implements ExtraView {

    private WeatherGadget weatherGadget = null;

    private CommunityEventsGadget communityEventsGadget = null;

    private final FlowPanel contentPanel;

    public ExtraViewImpl() {

        setStyleName(PortalRootPaneTheme.StyleName.ExtraGadget.name());

        contentPanel = new FlowPanel();
        add(contentPanel);
    }

    public void populate() {
        contentPanel.clear();
        if (weatherGadget == null && communityEventsGadget == null) {
            setVisible(false);
        } else {
            setVisible(true);
            if (weatherGadget != null) {
                contentPanel.add(weatherGadget);
            }
            if (communityEventsGadget != null) {
                contentPanel.add(communityEventsGadget);

            }

        }
    }

    @Override
    public void populateWeather(WeatherGadgetDTO notification) {
        if (notification != null) {
            weatherGadget = new WeatherGadget(notification);
            populate();
        }

    }

    @Override
    public void populateCommunityEvents(CommunityEventsGadgetDTO notification) {
        if (notification != null) {
            communityEventsGadget = new CommunityEventsGadget(notification);
            populate();
        }
    }
}
