/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal.l1;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;


@Transient
public interface L1FormFieldsData extends IEntity {

    IPrimitive<BigDecimal> totalRentOwing();

    IPrimitive<LogicalDate> totalRentOwingAsOf();

    IPrimitive<LogicalDate> fillingDate();

    L1RentalUnitInfo rentalUnitInfo();

    // Part2
    IList<L1TenantInfo> tenants();

    L1TenantContactInfo tenantContactInfo();

    // Part3
    L1ReasonForApplication reasonForApplication();

    // Part4
    L1OwedRent owedRent();

    L1OwedNsfCharges owedNsfCharges();

    // Part5
    L1OwedSummary owedSummary();

    // Part6
    /** L1 form can hold only one of these */
    IList<L1LandlordsContactInfo> landlordsContactInfos();

    L1AgentContactInfo agentContactInfo();

    // Part7
    L1SignatureData signatureData();

    // PAYMENT AND SCHEDULING FORM
    L1ScheduleAndPayment scheduleAndPayment();

}
