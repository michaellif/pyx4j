/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 4, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.prospect;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.adapters.index.AlphanumIndexAdapter;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseApplication;

public interface MasterOnlineApplication extends IEntity {

    @I18n(context = "MasterOnlineApplication")
    @XmlType(name = "MasterOnlineApplicationStatus")
    public enum Status {

        Incomplete, // Mapped to Lease status ApplicationInProgress

        Submitted, // LeaseApplication.Status.isDraft

        InformationRequested,

        Approved, // LeaseApplication.Status.Approved

        Cancelled; // LeaseApplication.Status.isDraft

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    @MemberColumn(notNull = true)
    LeaseApplication leaseApplication();

    @NotNull
    @ReadOnly
    @ToString
    @Length(14)
    @Indexed(uniqueConstraint = true, ignoreCase = true)
    @MemberColumn(sortAdapter = AlphanumIndexAdapter.class)
    IPrimitive<String> onlineApplicationId();

    IPrimitive<Status> status();

    @Detached
    Building building();

    @Detached
    Floorplan floorplan();

    @Owned
    @Detached
    ISet<OnlineApplication> applications();

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> createDate();
}
