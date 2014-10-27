/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.cards.simulator;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationDevelopmentFeature;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.RequireFeature;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@RequireFeature(ApplicationDevelopmentFeature.class)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "dev", namespace = VistaNamespace.operationsNamespace)
public interface CardServiceSimulationReconciliationRecord extends IEntity {

    IPrimitive<String> fileId();

    @Format("yyyy-MM-dd HH:mm")
    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    IPrimitive<LogicalDate> depositDate();

    CardServiceSimulationMerchantAccount merchant();

    IPrimitive<BigDecimal> totalDeposit();

    IPrimitive<BigDecimal> totalFee();

    IPrimitive<Integer> visaTransactions();

    IPrimitive<BigDecimal> visaDeposit();

    IPrimitive<BigDecimal> visaFee();

    IPrimitive<Integer> mastercardTransactions();

    IPrimitive<BigDecimal> mastercardDeposit();

    IPrimitive<BigDecimal> mastercardFee();

}
