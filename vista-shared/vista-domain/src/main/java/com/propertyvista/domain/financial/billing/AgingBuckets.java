/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 23, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.billing;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;

@Transient
public interface AgingBuckets extends IEntity {

    IPrimitive<DebitType> debitType();

    IPrimitive<BigDecimal> current();

    @Caption(name = "1 to 30")
    IPrimitive<BigDecimal> bucket30();

    @Caption(name = "30 to 60")
    IPrimitive<BigDecimal> bucket60();

    @Caption(name = "60 to 90")
    IPrimitive<BigDecimal> bucket90();
}
