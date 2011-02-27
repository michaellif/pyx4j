/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-24
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

public enum IncomeSource {

    //TODO i18n

    fulltime("Full time"),

    parttime("Part time"),

    selfemployed("Self employed"),

    seasonallyEmployed("Seasonally Employed"),

    socialServices("Social Services"),

    pension("Pension"),

    retired("Retired"),

    student("Student"),

    unemployment("Unemployment"),

    odsp("ODSP"),

    dividends("Dividends"),

    other("Other");

    private final String label;

    IncomeSource() {
        this.label = name();
    }

    IncomeSource(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

}
