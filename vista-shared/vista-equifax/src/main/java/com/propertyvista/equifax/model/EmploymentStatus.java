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
 * C - Self-Employed
 * E - Employed
 * H - Homemaker
 * K - Unemployed + No Income
 * Q - Not Asked
 * R - Retired
 * S - Full Time Student
 * U - Unemployed + Income
 * 
 * @author dmitry
 */
public enum EmploymentStatus implements EquifaxParameter {
    C("Self-Employed"), E("Employed"), H("Homemaker"), K("Unemployed + No Income"), Q("Not Asked"), R("Retired"), S("Full Time Student"), U(
            "Unemployed + Income");

    public final String description;

    EmploymentStatus(String description) {
        this.description = description;
    }

    public String getId() {
        return "P0003";
    }

    public String getValue() {
        return name();
    }
}
