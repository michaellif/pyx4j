/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.rpc.portal.resident.dto.CommunityEventDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.CommunityEventsGadgetDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.WeatherGadgetDTO;
import com.propertyvista.portal.rpc.portal.resident.services.ExtraActivityPortalService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.portal.server.portal.resident.services.weather.WeatherForecaster;
import com.propertyvista.server.common.util.AddressRetriever;

public class ExtraActivityPortalServiceImpl implements ExtraActivityPortalService {

    @Override
    public void retreiveWheather(AsyncCallback<WeatherGadgetDTO> callback) {

        AptUnit unit = ResidentPortalContext.getUnit();
        if (unit == null || unit.isEmpty()) {
            callback.onSuccess(null);
            return;
        }
        WeatherGadgetDTO forecasts = ServerSideFactory.create(WeatherForecaster.class).currentWeather(AddressRetriever.getUnitLegalAddress(unit));
        callback.onSuccess(forecasts);
    }

    @Override
    public void retreiveCommunityEvents(AsyncCallback<CommunityEventsGadgetDTO> callback) {
        if (!ApplicationMode.isDevelopment()) {
            callback.onSuccess(null);
            return;
        }
        // TODO: mock only
        AptUnit unit = ResidentPortalContext.getUnit();
        if (unit == null || unit.isEmpty()) {
            callback.onSuccess(null);
            return;
        }
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
            event.description().setValue("We've planned a fun event for you to come out, have fun and meet your neighbours.");
            data.events().add(event);
        }
        callback.onSuccess(data);
    }

}
