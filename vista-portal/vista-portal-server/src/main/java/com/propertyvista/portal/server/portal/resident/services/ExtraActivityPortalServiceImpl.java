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

import com.propertyvista.domain.property.asset.unit.AptUnit;
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
}
