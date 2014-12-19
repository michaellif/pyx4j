/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-21
 * @author ArtyomB
 */
package com.propertyvista.domain.legal.l1;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.legal.l1.L1ReasonForApplication.YesNo;

@Transient
public interface L1ApplicationSchedule extends IEntity {

    public enum ApplicationPackageDeliveryMethodToLandlord {

        Pickup, ByMail, ByFax

    }

    public enum ApplicationPackageDeliveryMethodToTenant {

        ByMail, ByCourier, ByAnotherMethod

    }

    IPrimitive<ApplicationPackageDeliveryMethodToLandlord> applicationPackageDeliveryMethodToLandlord();

    IPrimitive<LogicalDate> pickupDate();

    IPrimitive<String> officeName();

    IPrimitive<String> fax();

    IPrimitive<YesNo> isSameDayDeliveryToTenant();

    IPrimitive<LogicalDate> toTenantDeliveryDate();

    IPrimitive<ApplicationPackageDeliveryMethodToTenant> applicationPackageDeliveryMethodToTenant();

}
