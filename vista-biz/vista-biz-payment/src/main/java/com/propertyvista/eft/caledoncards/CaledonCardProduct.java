/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 13, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoncards;

public enum CaledonCardProduct {

    VisaCredit("VC"),

    VisaDebit("VD"),

    VisaBusiness("VB"),

    MasterCardCredit("MC"),

    MasterCardDebit("MD"),

    MasterCardBusiness("MB");

    private final String intrfaceId;

    CaledonCardProduct(String intrfaceId) {
        this.intrfaceId = intrfaceId;
    }

    public String getIntrfaceValue() {
        return intrfaceId;
    }

}
