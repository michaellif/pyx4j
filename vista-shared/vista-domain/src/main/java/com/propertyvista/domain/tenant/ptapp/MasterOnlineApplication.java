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
package com.propertyvista.domain.tenant.ptapp;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
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

import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.misc.EquifaxApproval;

public interface MasterOnlineApplication extends IEntity {

    @I18n(context = "MasterOnlineApplication")
    @XmlType(name = "MasterOnlineApplicationStatus")
    public enum Status {

        Incomplete, // Mapped to Lease status ApplicationInProgress

        Submitted, // LeaseApplication.Status.Draft

        InformationRequested,

        Cancelled; // LeaseApplication.Status.Draft

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Owner
    LeaseApplication leaseApplication();

    @NotNull
    @ReadOnly
    @ToString
    IPrimitive<String> onlineApplicationId();

    IPrimitive<Status> status();

    @Owned
    @Detached
    ISet<OnlineApplication> applications();

    @Timestamp(Update.Created)
    IPrimitive<LogicalDate> createDate();

    EquifaxApproval equifaxApproval();
}
