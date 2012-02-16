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
package com.propertyvista.domain.charges;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@ToStringFormat("${0}, {1}")
public interface ChargeLine extends Charge_OLD {

    @I18n
    public enum ChargeType {

        deposit,

        oneTimePayment,

        monthlyRent,

        firstMonthRent,

        prorated;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Editor(type = EditorType.label)
    @MemberColumn(name = "tp")
    IPrimitive<ChargeType> type();

    @Editor(type = EditorType.label)
    @ToString(index = 1)
    @MemberColumn(name = "lbl")
    @Caption(name = "Description")
    IPrimitive<String> label();
}
