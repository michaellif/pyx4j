/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.domain.payment;

public enum PaymentType {

    //TODO i18n

    Echeck("eCheck"),

    Visa,

    Amex,

    MasterCard,

    Discover,

    Interac;

    private final String label;

    PaymentType() {
        this.label = name();
    }

    PaymentType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

}
