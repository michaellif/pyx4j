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
package com.propertyvista.portal.rpc.portal.resident.dto;

import java.util.Date;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@Transient
public interface WeatherForecastDTO extends IEntity {

    @I18n
    public enum TemperatureUnit {

        Celcius, Fahrenheit;

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

    IPrimitive<TemperatureUnit> temperatureUnit();

    IPrimitive<String> weatherIconUrl();

    IPrimitive<String> weatherDescription();

}
