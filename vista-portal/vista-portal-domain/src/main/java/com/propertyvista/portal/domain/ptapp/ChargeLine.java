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
package com.propertyvista.portal.domain.ptapp;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translation;

@ToStringFormat("{0} {1}")
public interface ChargeLine extends Charge {

    public enum ChargeType {

        @Translation("Deposit")
        deposit,

        @Translation("Application Fee")
        applicationFee,

        @Translation("Monthly Rent")
        rent,

        @Translation("Monthly Rent")
        firstMonthRent,

        @Translation("Parking")
        parking,

        @Translation("Second Parking")
        parking2,

        @Translation("Locker")
        locker,

        @Translation("Pet Deposit")
        petDeposit,

        @Translation("Pet Charge")
        petCharge,

        @Translation("Extra Parking")
        extraParking,

        @Translation("Extra Locker")
        extraLocker,

        @Translation("Cable TV")
        cableTv,

        @Translation("Prorated")
        prorated;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Editor(type = EditorType.label)
    @MemberColumn(name = "tp")
    IPrimitive<ChargeType> type();

    @Editor(type = EditorType.label)
    @ToString(index = 1)
    @MemberColumn(name = "lbl")
    IPrimitive<String> label();
}
