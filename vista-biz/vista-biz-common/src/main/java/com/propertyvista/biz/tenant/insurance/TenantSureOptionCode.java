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

/** Describes coverage options for TenantSure */
public enum TenantSureOptionCode {

    //@formatter:off
    TSP10("1000000", null),
    TSP20("2000000", null),
    TSP50("5000000", null),
    TSP110("1000000", "10000"),
    TSP120("1000000", "20000"),
    TSP130("1000000", "30000"),
    TSP140("1000000", "40000"),
    TSP150("1000000", "50000"),
    TSP160("1000000", "60000"),
    TSP210("2000000", "10000"),
    TSP220("2000000", "20000"),
    TSP230("2000000", "30000"),
    TSP240("2000000", "40000"),
    TSP250("2000000", "50000"),
    TSP260("2000000", "60000"),
    TSP510("5000000", "10000"),
    TSP520("5000000", "20000"),
    TSP530("5000000", "30000"),
    TSP540("5000000", "40000"),
    TSP550("5000000", "50000"),
    TSP560("5000000", "60000");
    //@formatter:on

    private BigDecimal generalLiability;

    private BigDecimal contentsCoverage;

    TenantSureOptionCode(String generalLiabilityR, String contentsCoverageR) {
        this.generalLiability = new BigDecimal(generalLiabilityR);
        this.contentsCoverage = contentsCoverageR != null ? new BigDecimal(contentsCoverageR) : BigDecimal.ZERO;
        assert (generalLiability.compareTo(BigDecimal.ZERO) >= 0 & contentsCoverage.compareTo(BigDecimal.ZERO) >= 0) : "coverage value must be a non-negative value";
    }

    /**
     * Converts coverage amounts to code
     * 
     * @param generalLiability
     *            liability amount
     * @param contents
     *            contents liability about
     * @return code of TenantSure option
     * @throws IllegalArgumentException
     *             if there is no code that matches the requested liability amounts.
     */
    public static TenantSureOptionCode codeOf(BigDecimal generalLiability, BigDecimal contents) throws IllegalArgumentException {
        String optionCodeStr = makeOptionCode(generalLiability, contents);
        return TenantSureOptionCode.valueOf(optionCodeStr);
    }

    public BigDecimal generalLiability() {
        return generalLiability;
    }

    public BigDecimal contentsCoverage() {
        return contentsCoverage;
    }

    private static String makeOptionCode(BigDecimal generalLiability, BigDecimal contentsCoverage) {
        if (generalLiability == null) {
            throw new IllegalArgumentException();
        }
        StringBuilder optionCode = new StringBuilder();
        optionCode.append("TSP");
        optionCode.append(firstDigit(generalLiability));
        if (contentsCoverage != null && contentsCoverage.compareTo(BigDecimal.ZERO) != 0) {
            optionCode.append(firstDigit(contentsCoverage));
        }
        optionCode.append("0");
        return optionCode.toString();
    }

    private static String firstDigit(BigDecimal number) {
        return number.toString().substring(0, 1);
    }
}
