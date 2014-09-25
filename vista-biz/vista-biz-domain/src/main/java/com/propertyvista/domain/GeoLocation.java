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

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

//TODO VladL move this class to DTO
@ToStringFormat("{0} {1} & {2} {3}")
@Transient
public interface GeoLocation extends IEntity {

    @I18n
    public enum LatitudeDirection {
        North, South;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum LongitudeDirection {
        East, West;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    // ---------------------------------------

    @ToString(index = 0)
    @Format("#0.000000")
    IPrimitive<Double> latitude();

    @ToString(index = 1)
    IPrimitive<LatitudeDirection> latitudeDirection();

    @ToString(index = 2)
    @Format("#0.000000")
    IPrimitive<Double> longitude();

    @ToString(index = 3)
    IPrimitive<LongitudeDirection> longitudeDirection();
}
