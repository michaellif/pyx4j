/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-08
 * @author vlads
 */
package com.propertyvista.operations.domain.eft.cards.simulator;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.config.shared.ApplicationDevelopmentFeature;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.RequireFeature;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@RequireFeature(ApplicationDevelopmentFeature.class)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "dev", namespace = VistaNamespace.operationsNamespace)
public interface CardServiceSimulationMerchantAccount extends IEntity {

    @Indexed(group = "un,2", uniqueConstraint = true)
    @Editor(type = EditorType.label)
    CardServiceSimulationCompany company();

    @ToString(index = 1)
    @Indexed(group = "un,1", uniqueConstraint = true)
    @NotNull
    @Length(8)
    IPrimitive<String> terminalID();

    @ToString(index = 2)
    @Editor(type = EditorType.money)
    @Format("#0.00")
    IPrimitive<BigDecimal> balance();

    @Caption(description = "Force rejection code on all transactions to this Account")
    IPrimitive<String> responseCode();

    @Editor(type = EditorType.percentage)
    @Format("#,##0.00")
    @MemberColumn(scale = 4)
    IPrimitive<BigDecimal> visaCreditConvenienceFee();

    @Editor(type = EditorType.percentage)
    @Format("#,##0.00")
    @MemberColumn(scale = 4)
    IPrimitive<BigDecimal> masterCardConvenienceFee();

    @Editor(type = EditorType.percentage)
    @Format("#,##0.00")
    @MemberColumn(scale = 4)
    IPrimitive<BigDecimal> visaDebitConvenienceFee();

    @Editor(type = EditorType.percentage)
    @Format("#,##0.00")
    @MemberColumn(scale = 4)
    IPrimitive<BigDecimal> visaCreditFee();

    @Editor(type = EditorType.percentage)
    @Format("#,##0.00")
    @MemberColumn(scale = 4)
    IPrimitive<BigDecimal> masterCardFee();

    @Editor(type = EditorType.percentage)
    @Format("#,##0.00")
    @MemberColumn(scale = 4)
    IPrimitive<BigDecimal> visaDebitFee();

    @Timestamp(Update.Created)
    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> created();

    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = CardServiceSimulationTransaction.class)
    ISet<CardServiceSimulationTransaction> transactions();
}
