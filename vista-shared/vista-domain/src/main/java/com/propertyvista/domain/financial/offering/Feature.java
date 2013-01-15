/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 22, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.offering;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.offering.Feature.FeatureV;

@ToStringFormat("{1}, {0}")
@DiscriminatorValue("feature")
public interface Feature extends Product<FeatureV> {

    @I18n
    @XmlType(name = "FeatureType")
    public enum Type {

        parking(true, true),

        locker(true, true),

        pet(true, true),

        @Translate("Add-On")
        addOn(true, true),

        utility(true, true),

        oneTimeCharge(true, false),

        booking(false, false);

        private final boolean recurrent;

        private final boolean inAgreement;

        Type(boolean inAgreement, boolean recurrent) {
            this.inAgreement = inAgreement;
            this.recurrent = recurrent;
        }

        public boolean isInAgreement() {
            return inAgreement;
        };

        public boolean isRecurrent() {
            return recurrent;
        };

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        public static Collection<Type> nonMandatory() {
            return EnumSet.of(pet);
        }

        public static Collection<Type> nonReccuring() {
            return EnumSet.of(booking);
        }
    }

    @Timestamp
    IPrimitive<Date> updated();

    @NotNull
    @ToString(index = 0)
    @MemberColumn(notNull = true)
    IPrimitive<Type> featureType();

    @DiscriminatorValue("feature")
    public interface FeatureV extends Product.ProductV<Feature> {

        IPrimitive<Boolean> recurring();

        IPrimitive<Boolean> mandatory();
    }

}
