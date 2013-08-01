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
package com.propertyvista.portal.rpc.portal.dto;

import java.util.Date;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@Transient
public interface WeatherForecastDTO extends IEntity {

    // this is taken from http://api.accuweather.com/developers/weatherIcons
    // TODO finish this (see later)
    @I18n
    public enum WeatherDescription {

        Sunny, MostlySunny, PartlySunny, IntermittentClouds, HazySunshine, MostlyCloudy, Cloudy, Dreary, Fog, Showers;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };
    }

    IPrimitive<String> providerName();

    IPrimitive<String> linkToProvidersWebstite();

    IPrimitive<Date> from();

    IPrimitive<Date> to();

    IPrimitive<Double> temperature();

    IPrimitive<String> temperatureUnit();

    // TODO change to enum
    IPrimitive<String> weatherDescription();

}
