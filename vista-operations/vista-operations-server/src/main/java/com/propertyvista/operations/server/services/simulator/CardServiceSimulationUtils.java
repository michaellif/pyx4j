/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services.simulator;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.eft.caledoncards.CaledonTokenAction;
import com.propertyvista.eft.caledoncards.CaledonTransactionType;
import com.propertyvista.eft.caledoncards.dev.VisaDebitInternalValidator;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulatorConfig;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulationTransaction.SimpulationTransactionType;

public class CardServiceSimulationUtils {

    public static CardServiceSimulatorConfig getCardServiceSimulatorConfig() {
        CardServiceSimulatorConfig simulatorConfig = Persistence.service().retrieve(EntityQueryCriteria.create(CardServiceSimulatorConfig.class));
        if (simulatorConfig == null) {
            simulatorConfig = EntityFactory.create(CardServiceSimulatorConfig.class);
        }
        if (simulatorConfig.responseType().isNull()) {
            simulatorConfig.responseType().setValue(CardServiceSimulatorConfig.SimpulationType.SimulateTransations);
        }
        if (simulatorConfig.acceptCardExpiryFrom().isNull()) {
            simulatorConfig.acceptCardExpiryFrom().setValue(new LogicalDate(CardServiceSimulationUtils.getExpiryMonthStart()));
        }
        if (simulatorConfig.acceptCardExpiryTo().isNull()) {
            simulatorConfig.acceptCardExpiryTo().setValue(new LogicalDate(DateUtils.addYears(getExpiryMonthStart(), 5)));
        }
        return simulatorConfig;
    }

    static Date getExpiryMonthStart() {
        Calendar c = Calendar.getInstance();
        c.setTime(SystemDateManager.getDate());
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    static BigDecimal parsAmount(String value) {
        if (CommonsStringUtils.isEmpty(value)) {
            return null;
        } else {
            String valueCents;
            String valueDollars;
            int len = value.length();
            if (len == 1) {
                valueCents = "0" + value;
                valueDollars = "0";
            } else if (len == 2) {
                valueCents = value;
                valueDollars = "0";
            } else {
                valueCents = value.substring(len - 2, len);
                valueDollars = value.substring(0, len - 2);
            }

            BigDecimal money = new BigDecimal(valueDollars + "." + valueCents);
            return money.setScale(2);
        }
    }

    static boolean isValidAmount(String value) {
        if (CommonsStringUtils.isEmpty(value) || (!NumberUtils.isNumber(value))) {
            return false;
        } else {
            return true;
        }
    }

    static LogicalDate parsDate(String expiryDate) {
        try {
            Date date = new SimpleDateFormat("MMyydd").parse(expiryDate + "01");
            return new LogicalDate(date);
        } catch (ParseException e) {
            throw new Error(e);
        }
    }

    static CaledonTokenAction toCaledonTokenAction(String tokenAction) {
        for (CaledonTokenAction type : EnumSet.allOf(CaledonTokenAction.class)) {
            if (type.getValue().equals(tokenAction)) {
                return type;
            }
        }
        throw new Error("Invalid tokenAction '" + tokenAction + "'");
    }

    static CaledonTransactionType toCaledonTransactionType(String transactionTypeChar) {
        for (CaledonTransactionType transactionType : EnumSet.allOf(CaledonTransactionType.class)) {
            if (transactionType.getValue().equals(transactionTypeChar)) {
                return transactionType;
            }
        }
        throw new Error("Invalid transactionType '" + transactionTypeChar + "'");
    }

    static SimpulationTransactionType toSimTransactionType(String transactionTypeChar) {
        return toSimTransactionType(toCaledonTransactionType(transactionTypeChar));
    }

    static SimpulationTransactionType toSimTransactionType(CaledonTransactionType transactionType) {
        switch (transactionType) {
        case SALE:
            return SimpulationTransactionType.sale;
        case PREAUTH:
            return SimpulationTransactionType.preAuthorization;
        case AUTH_REVERSE:
            return SimpulationTransactionType.preAuthorizationReversal;
        case COMPLETION:
            return SimpulationTransactionType.completion;
        case VOID:
            return SimpulationTransactionType.returnVoid;
        default:
            throw new Error("Unsupported transactionType '" + transactionType + "'");
        }
    }

    static CreditCardType detectCardType(String creditCardNumber) {
        for (CreditCardType type : CreditCardType.values()) {
            if (ValidationUtils.isCreditCardNumberIinValid(type.iinsPatterns, creditCardNumber)) {
                if ((type == CreditCardType.Visa) && VisaDebitInternalValidator.isVisaDebitValid(creditCardNumber)) {
                    return CreditCardType.VisaDebit;
                } else {
                    return type;
                }
            }
        }
        return null;
    }
}
