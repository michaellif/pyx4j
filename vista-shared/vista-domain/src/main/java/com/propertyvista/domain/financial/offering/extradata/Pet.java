/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-14
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.domain.financial.offering.extradata;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.financial.offering.ChargeItemExtraData;

@DiscriminatorValue("Pet_ChargeItemExtraData")
public interface Pet extends ChargeItemExtraData {

    @I18nComment("Weight unit")
    public enum WeightUnit {

        @Translate("LB")
        lb,

        @Translate("KG")
        kg;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @BusinessEqualValue
    public IPrimitive<String> name();

    @NotNull
    public IPrimitive<String> color();

    public IPrimitive<String> breed();

    @NotNull
    public IPrimitive<Integer> weight();

    @Caption(name = "Units")
    @NotNull
    public IPrimitive<WeightUnit> weightUnit();

    @NotNull
    @Format("MM/dd/yyyy")
    @BusinessEqualValue
    public IPrimitive<LogicalDate> birthDate();

    @EmbeddedEntity
    @Caption(name = "Charge")
    public ChargeLine chargeLine();

}
