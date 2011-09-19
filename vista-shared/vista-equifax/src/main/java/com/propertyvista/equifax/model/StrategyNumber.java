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

public class StrategyNumber implements EquifaxParameter {
    private int number;

    public StrategyNumber(int number) {
        if (number < 1 || number > 99) {
            throw new IllegalArgumentException("Strategy number must be between 1 and 99");
        }
        this.number = number;
    }

    public String getId() {
        return "P0001";
    }

    public String getValue() {
        if (number < 10)
            return "0" + number;
        return "" + number;
    }
}
