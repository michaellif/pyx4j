/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent.ChangeType;

import com.propertyvista.portal.domain.dto.CommunityEventDTO;
import com.propertyvista.portal.domain.dto.extra.CommunityEventsGadgetDTO;
import com.propertyvista.portal.domain.dto.extra.ExtraGadgetDTO;
import com.propertyvista.portal.domain.dto.extra.WeatherGadgetDTO;
import com.propertyvista.portal.domain.dto.extra.WeatherGadgetDTO.WeatherType;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.ui.extra.ExtraView;
import com.propertyvista.portal.web.client.ui.extra.ExtraView.ExtraPresenter;

public class ExtraActivity extends AbstractActivity implements ExtraPresenter {

    private static List<ExtraGadgetDTO> gadgets = new ArrayList<ExtraGadgetDTO>();

    private final ExtraView view;

    static {

        {
            WeatherGadgetDTO data = EntityFactory.create(WeatherGadgetDTO.class);
            data.weatherType().setValue(WeatherType.sunny);
            data.temperature().setValue(25);

            //TODO implement WeatherGadget
            if (false) {
                gadgets.add(data);
            }

        }

        {
            CommunityEventsGadgetDTO data = EntityFactory.create(CommunityEventsGadgetDTO.class);

            {
                CommunityEventDTO event = EntityFactory.create(CommunityEventDTO.class);
                event.caption().setValue("Community Garage Sale");
                event.timeAndLocation().setValue("June 10th, 8:00 - 3:00");
                event.description().setValue("Clean out the attic. It's time to turn your unused items into cash. Signs will be posted around the community.");
                data.events().add(event);
            }

            {
                CommunityEventDTO event = EntityFactory.create(CommunityEventDTO.class);
                event.caption().setValue("Summerfest is fast approaching!");
                event.timeAndLocation().setValue("August 31st from 11:00am-4:00pm at Central Park");
                event.description().setValue("We’ve planned a fun event for you to come out, have fun and meet your neighbours.");
                data.events().add(event);
            }

            //TODO implement CommunityEventsGadget
            if (false) {
                gadgets.add(data);
            }
        }

    }

    public ExtraActivity(Place place) {
        view = PortalWebSite.getViewFactory().instantiate(ExtraView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
        view.populate(gadgets);
    }
}
