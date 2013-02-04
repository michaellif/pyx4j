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
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Some money values from CFC come as Strings, this class provides methods to handle that situation and convert them to BigDecimal's */
public class TenantSureCfcMoneyAdapter {

    public static BigDecimal parseMoney(String moneyAmountRepr) throws NumberFormatException {
        BigDecimal moneyAmount = null;
        moneyAmount = new BigDecimal(moneyAmountRepr);
        return moneyAmount.setScale(2, RoundingMode.HALF_UP);
    }

}
