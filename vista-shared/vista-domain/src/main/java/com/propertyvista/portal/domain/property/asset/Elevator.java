/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.domain.property.asset;

import java.util.Date;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.common.domain.financial.Money;
import com.propertyvista.portal.domain.property.vendor.Contract;

public interface Elevator extends IEntity {

        IPrimitive<String> description();

        @MemberColumn(name = "elevatorType")
        IPrimitive<String> type();

        IPrimitive<String> make();

        @MemberColumn(name = "elevatorYear")
        IPrimitive<Date> year();

        IPrimitive<String> build();

        IPrimitive<String> warrantee();

        IPrimitive<Date> licenceExpiration();

        Contract maitenanceContractor();

        IPrimitive<Boolean> usedForMoveInOut();

        Money bookingDeposit();

        IPrimitive<String> bookingRestrictoion();

// TODO create some notes object/domain which defines list of notes with dates and creators (one user can't delete notes of the others)...
        IPrimitive<String> notes();
    }