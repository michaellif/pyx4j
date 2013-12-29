/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@Transient
public interface WeatherGadgetDTO extends ExtraGadgetDTO {

    @I18n
    public enum WeatherType {
        sunny, fair, partlyCloudy, mostlyCloudy, cloudy, fog, lightShowers, showers, thunderShowers, rainAndSnow, flurries, snow;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    IPrimitive<WeatherType> weatherType();

    IPrimitive<Integer> temperature();

}
