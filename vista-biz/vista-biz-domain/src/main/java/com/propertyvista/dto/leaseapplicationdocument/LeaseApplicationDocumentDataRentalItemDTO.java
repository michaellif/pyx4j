/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2014
 * @author ArtyomB
 */
package com.propertyvista.dto.leaseapplicationdocument;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface LeaseApplicationDocumentDataRentalItemDTO extends IEntity {

    IPrimitive<String> item();

    IPrimitive<BigDecimal> price();

    IPrimitive<LogicalDate> effectiveDate();

    IPrimitive<LogicalDate> expirationDate();

}
