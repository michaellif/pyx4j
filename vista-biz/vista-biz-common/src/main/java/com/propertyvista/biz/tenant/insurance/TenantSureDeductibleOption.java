/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-31
 * @author ArtyomB
 */
package com.propertyvista.biz.tenant.insurance;

import java.math.BigDecimal;

public enum TenantSureDeductibleOption {

    DEFAULT(true, "500"), DEDUCTIBLE_1000("1000"), DEDUCTIBLE_2500("2500");

    private static class THE_ONE_AND_THE_ONLY {
        static int one = 0;
    }

    private boolean isDefault;

    private BigDecimal amount;

    TenantSureDeductibleOption(boolean isDefault, String amount) {
        this.isDefault = isDefault;
        this.amount = new BigDecimal(amount);
        assert (this.amount.compareTo(BigDecimal.ZERO) >= 0) : "deductible should be a non-negative value";
        assert ((this.isDefault && (THE_ONE_AND_THE_ONLY.one++ == 0)) || !this.isDefault) : "tenant sure default deductible option must be unique";
    }

    TenantSureDeductibleOption(String amount) {
        this(false, amount);
    }

    public BigDecimal amount() {
        return amount;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public static TenantSureDeductibleOption deductibleOf(BigDecimal amount) throws IllegalArgumentException {
        if (amount == null) {
            throw new NullPointerException();
        }
        for (TenantSureDeductibleOption option : TenantSureDeductibleOption.values()) {
            if (option.amount().compareTo(amount) == 0) {
                return option;
            }
        }
        throw new IllegalArgumentException("deductible option that matches the requested amount does not exist");
    }

    public static BigDecimal[] amountValues() {
        TenantSureDeductibleOption[] vals = TenantSureDeductibleOption.values();

        BigDecimal[] d = new BigDecimal[vals.length];
        for (int i = 0; i < vals.length; ++i) {
            d[i] = vals[i].amount();
        }

        return d;
    }
}
