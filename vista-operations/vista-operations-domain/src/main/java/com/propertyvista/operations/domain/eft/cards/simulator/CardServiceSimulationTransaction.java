/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-06
 * @author vlads
 */
package com.propertyvista.operations.domain.eft.cards.simulator;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.config.shared.ApplicationDevelopmentFeature;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RequireFeature;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@RequireFeature(ApplicationDevelopmentFeature.class)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "dev", namespace = VistaNamespace.operationsNamespace)
public interface CardServiceSimulationTransaction extends IEntity {

    public enum SimulationTransactionType {

        Sale,

        Void,

        Return,

        PreAuthorization,

        PreAuthorizationReversal,

        Completion;

    }

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    CardServiceSimulationCard card();

    @JoinColumn
    CardServiceSimulationMerchantAccount merchant();

    @MemberColumn(name = "tp")
    IPrimitive<SimulationTransactionType> transactionType();

    @Caption(description = "Force rejection code on next transaction (of this type)")
    IPrimitive<Boolean> scheduledSimulatedResponce();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> convenienceFee();

    @Length(60)
    IPrimitive<String> reference();

    @Caption(description = "'0000' Is OK")
    IPrimitive<String> responseCode();

    IPrimitive<String> authorizationNumber();

    @MemberColumn(notNull = true)
    IPrimitive<Boolean> voided();

    @Format("MM/dd/yyyy HH:mm.ss")
    IPrimitive<Date> transactionDate();
}
