/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-04
 * @author ArtyomB
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Some money values from CFC come as Strings, this class provides methods to handle that situation and convert them to BigDecimal's.
 * Also sets up a general policy on how to handle money values
 */
// TODO maybe shouldn't be static and should be a facade?
public class TenantSureCfcMoneyAdapter {

    public static RoundingMode getRoundingMode() {
        return RoundingMode.HALF_UP;
    }

    public static BigDecimal parseMoney(String moneyAmountRepr) throws NumberFormatException {
        BigDecimal moneyAmount = null;
        moneyAmount = moneyAmountRepr != null ? new BigDecimal(moneyAmountRepr) : new BigDecimal("0.00");
        return adoptMoney(moneyAmount);
    }

    public static BigDecimal adoptMoney(BigDecimal amount) {
        return amount.setScale(2, getRoundingMode());
    }

}
