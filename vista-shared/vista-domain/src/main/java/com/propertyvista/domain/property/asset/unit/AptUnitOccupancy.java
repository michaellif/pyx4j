/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.unit;

import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.tenant.lease.Lease;

public interface AptUnitOccupancy extends IEntity {

    @Translatable
    public enum StatusType {

        leased,

        available,

        reserved,

        vacant,

        offMarket;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    public enum OffMarketType {

        down,

        model,

        employee,

        construction,

        office,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Owner
//    @Detached
    AptUnit unit();

    IPrimitive<java.sql.Date> dateFrom();

    /**
     * What if there is no limit to this date, what should it be then?
     */
    IPrimitive<java.sql.Date> dateTo();

    IPrimitive<StatusType> status();

    IPrimitive<OffMarketType> offMarket();

    /**
     * Would be good to have an example of a description for occupancy in Java
     * Doc
     */
    IPrimitive<String> description();

    Lease lease();
}
