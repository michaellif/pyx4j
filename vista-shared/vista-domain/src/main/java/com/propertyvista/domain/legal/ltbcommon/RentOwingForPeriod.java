/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-20
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal.ltbcommon;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface RentOwingForPeriod extends IEntity {

    IPrimitive<LogicalDate> from();

    IPrimitive<LogicalDate> to();

    IPrimitive<BigDecimal> rentCharged();

    IPrimitive<BigDecimal> rentPaid();

    IPrimitive<BigDecimal> rentOwing();

}
