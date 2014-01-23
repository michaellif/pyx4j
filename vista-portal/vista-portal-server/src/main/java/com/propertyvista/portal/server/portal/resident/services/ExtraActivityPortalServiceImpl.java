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

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.tenant.communityevent.CommunityEventFacade;
import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.CommunityEventDTO;
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

        AptUnit unit = ResidentPortalContext.getUnit();
        if (unit == null || unit.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        List<CommunityEvent> events = ServerSideFactory.create(CommunityEventFacade.class).getCommunityEvents(unit.building());
        if (events == null || events.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        CommunityEventsGadgetDTO data = EntityFactory.create(CommunityEventsGadgetDTO.class);
        for (CommunityEvent from : events) {
            CommunityEventDTO to = EntityFactory.create(CommunityEventDTO.class);
            to.caption().setValue(from.caption().getValue());
            to.timeAndLocation().setValue(from.timeAndLocation().getValue());
            to.description().setValue(from.description().getValue());
            data.events().add(to);
        }

        callback.onSuccess(data);
    }

}
