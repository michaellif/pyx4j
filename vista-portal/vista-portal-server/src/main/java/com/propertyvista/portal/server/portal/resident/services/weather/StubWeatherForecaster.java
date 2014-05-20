/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-01
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.weather;

import java.util.Collections;
import java.util.List;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.portal.rpc.portal.resident.dto.WeatherGadgetDTO;

public class StubWeatherForecaster implements WeatherForecaster {

    @Override
    public List<WeatherGadgetDTO> forecastWeather(InternationalAddress address) {
        return Collections.emptyList();
    }

    @Override
    public WeatherGadgetDTO currentWeather(InternationalAddress address) {
        // TODO Auto-generated method stub
        return null;
    }

}
