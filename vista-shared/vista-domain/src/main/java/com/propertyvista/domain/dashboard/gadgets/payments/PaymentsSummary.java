/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 31, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.payments;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;

public interface PaymentsSummary extends IEntity {

    /** The date when a summary was taken */
    IPrimitive<LogicalDate> timestamp();

    /** The date for which a summary is applied to */
    IPrimitive<LogicalDate> snapshotDay();

    /** This one is for holding a merchant account */
    MerchantAccount merchantAccount();

    IPrimitive<PaymentRecord.PaymentStatus> status();

    IPrimitive<BigDecimal> cash();

    IPrimitive<BigDecimal> cheque();

    IPrimitive<BigDecimal> eCheque();

    @Caption(name = "EFT")
    IPrimitive<BigDecimal> eft();

    @Caption(name = "CC")
    IPrimitive<BigDecimal> cc();

    IPrimitive<BigDecimal> interac();

}
