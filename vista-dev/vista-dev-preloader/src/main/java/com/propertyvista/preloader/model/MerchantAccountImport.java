/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.preloader.model;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.rpc.ImportColumn;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.financial.AbstractMerchantAccount;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Transient
public interface MerchantAccountImport extends AbstractMerchantAccount {

    IPrimitive<Boolean> ignore();

    IPrimitive<String> companyId();

    @Override
    @Caption(name = "Terminal Id")
    IPrimitive<String> merchantTerminalId();

    @ImportColumn(names = "Terminal Id Convenience Fee")
    IPrimitive<String> merchantTerminalIdConvenienceFee();

    @Override
    IPrimitive<String> bankId();

    @Override
    @Caption(name = "Branch #")
    IPrimitive<String> branchTransitNumber();

    @Override
    @Caption(name = "Account #")
    IPrimitive<String> accountNumber();

    IPrimitive<BigDecimal> transactionFee();

    IPrimitive<BigDecimal> rejectFee();

    IPrimitive<BigDecimal> returnFee();
}
