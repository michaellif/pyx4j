/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 18, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.equifax.model;

/**
 * @author dmitry
 */
public class MonthlyHousingCosts implements EquifaxParameter {

    private final int amount;

    public MonthlyHousingCosts(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Monthly housing costs must be a positive number");
        }
        this.amount = amount;
    }

    @Override
    public String getId() {
        return "P0013";
    }

    @Override
    public String getValue() {
        return String.valueOf(amount);
    }
}
