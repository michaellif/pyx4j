/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.shared.adapters.ParkingSpotCountersAdapter;

public interface ParkingSpot extends IEntity {

    @I18n
    @XmlType(name = "ParkingSpotType")
    public enum Type {

        regular,

        wide,

        narrow,

        disabled;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    Parking parking();

    @NotNull
    @ToString(index = 1)
    @MemberColumn(name = "spotType", modificationAdapters = { ParkingSpotCountersAdapter.class })
    IPrimitive<Type> type();

    @NotNull
    @ToString(index = 0)
    IPrimitive<String> name();
}
