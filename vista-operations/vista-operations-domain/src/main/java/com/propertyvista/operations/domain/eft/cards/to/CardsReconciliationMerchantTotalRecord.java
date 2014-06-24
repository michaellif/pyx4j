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
package com.propertyvista.operations.domain.eft.cards.to;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

/**
 * Caledon interface CSV record structure
 */
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Transient
public interface CardsReconciliationMerchantTotalRecord extends IEntity {

    enum MerchantTotalRecordType {
        Deposit, Fees, Chargeback, Adjustment
    }

    @NotNull
    IPrimitive<LogicalDate> date();

    @NotNull
    IPrimitive<String> merchantID();

    @NotNull
    IPrimitive<String> terminalID();

    @NotNull
    IPrimitive<MerchantTotalRecordType> type();

    @NotNull
    IPrimitive<BigDecimal> credit();

    @NotNull
    IPrimitive<BigDecimal> debit();

}
