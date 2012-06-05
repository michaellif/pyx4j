package com.propertyvista.domain.financial.billing;

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2012
 * @author michaellif
 * @version $Id$
 */

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.shared.IPrimitive;

/**
 * Account Charge - total charge for a Lease Adjustment
 * 
 */
@DiscriminatorValue("CarryforwardCharge")
public interface InvoiceCarryforwardCharge extends InvoiceCharge {

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> targetDate();

}
