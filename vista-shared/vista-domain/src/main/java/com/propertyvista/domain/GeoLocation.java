/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain;

import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

@ToStringFormat("{0} {1} & {2} {3}")
public interface GeoLocation extends IEntity {

    @Translatable
    public enum LatitudeType {
        North, South;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    public enum LongitudeType {
        East, West;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    // ---------------------------------------

    @ToString(index = 0)
    IPrimitive<Double> latitude();

    @NotNull
    @ToString(index = 1)
    IPrimitive<LatitudeType> latitudeType();

    @ToString(index = 2)
    IPrimitive<Double> longitude();

    @NotNull
    @ToString(index = 3)
    IPrimitive<LongitudeType> longitudeType();
}
