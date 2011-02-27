/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-16
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.shared.IPrimitive;

public interface ChargeLine extends Charge {

    public enum ChargeType {
        deposit("Deposit"),

        applicationFee("Application Fee"),

        rent("Monthly Rent"),

        firstMonthRent("Monthly Rent"),

        parking("Parking"),

        parking2("Second Parking"),

        locker("Locker"),

        petDeposit("Pet Deposit"),

        petCharge("Pet Charge"),

        extraParking("Extra Parking"),

        extraLocker("Extra Locker"),

        cableTv("Cable TV"),

        prorated("Prorated");

        private final String label;

        ChargeType() {
            this.label = name();
        }

        ChargeType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @Editor(type = EditorType.label)
    IPrimitive<ChargeType> type();

    @Editor(type = EditorType.label)
    IPrimitive<String> label();
}
