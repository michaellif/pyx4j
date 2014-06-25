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
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.rpc.ImportColumn;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

/**
 * Caledon interface CSV record structure
 */
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Transient
public interface CardsReconciliationCardTotalRecord extends IEntity {

    enum CardTotalRecordType {

        VisaDeposit, VisaFees, MastercardDeposit, MastercardFees, Chargeback, Adjustment;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @ImportColumn(format = "MM/dd/yyyy")
    @Format("MM/dd/yyyy")
    @ToString
    IPrimitive<LogicalDate> date();

    @NotNull
    @ImportColumn
    IPrimitive<String> merchantID();

    @NotNull
    @ImportColumn
    @ToString
    IPrimitive<String> terminalID();

    @NotNull
    @ImportColumn
    @ToString
    IPrimitive<CardTotalRecordType> type();

    @ImportColumn
    IPrimitive<BigDecimal> credit();

    @ImportColumn
    IPrimitive<BigDecimal> debit();

}
