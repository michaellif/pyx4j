/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 24, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.cards;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;

@Table(namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CardsReconciliationRecord extends IEntity {

    IPrimitive<LogicalDate> date();

    //ID Copied from the file not used in Vista
    IPrimitive<String> merchantID();

    @ToString
    IPrimitive<String> merchantTerminalId();

    // found based on merchantTerminalId
    @Indexed
    PmcMerchantAccountIndex merchantAccount();

    IPrimitive<Boolean> convenienceFeeAccount();

    IPrimitive<CardsReconciliationRecordProcessingStatus> status();

    IPrimitive<BigDecimal> totalDeposit();

    IPrimitive<BigDecimal> totalFee();

    IPrimitiveSet<BigDecimal> chargebacks();

    IPrimitiveSet<BigDecimal> adjustments();

    IPrimitive<BigDecimal> visaDeposit();

    IPrimitive<BigDecimal> visaFee();

    IPrimitive<BigDecimal> mastercardDeposit();

    IPrimitive<BigDecimal> mastercardFee();

    CardsReconciliationFile fileMerchantTotal();

    CardsReconciliationFile fileCardTotal();
}
