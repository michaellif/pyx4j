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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.tenant.lease.Lease;

public interface AptUnitOccupancy extends IEntity {

    @I18n
    public enum Status {

        leased,

        available,

        reserved,

        vacant,

        offMarket;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n
    public enum OffMarketType {

        down,

        model,

        employee,

        construction,

        office,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @Detached
    @ReadOnly
    AptUnit unit();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> dateFrom();

    /**
     * What if there is no limit to this date, what should it be then?
     */
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> dateTo();

    IPrimitive<Status> status();

    IPrimitive<OffMarketType> offMarket();

    /**
     * Would be good to have an example of a description for occupancy in Java
     * Doc
     */
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    Lease lease();
}
