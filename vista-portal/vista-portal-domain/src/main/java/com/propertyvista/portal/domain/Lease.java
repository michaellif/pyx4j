/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.domain;

import java.util.Date;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

public interface Lease extends IEntity {

    ApptUnit unit();

    ISet<Tenant> tenants();

    Money currentRent();

    IPrimitive<Date> expectedMoveInDate();

    IPrimitive<Date> expectedMoveOutDate();

    IPrimitive<Date> leaseFromDate();

    IPrimitive<Date> leaseToDate();

    IPrimitive<Date> actualMoveIn();

    IPrimitive<Date> actualMoveOut();

    IPrimitive<Boolean> responsibleForLease();

    IPrimitive<Date> leaseSignDate();

    /**
     * (max 50 char)
     */
    IPrimitive<String> specialStatus();

    /**
     * (max 50 char)
     */
    IPrimitive<String> paymentAccepted();

    /**
     * (max 50 char)
     */
    IPrimitive<String> accountNumber();

}
