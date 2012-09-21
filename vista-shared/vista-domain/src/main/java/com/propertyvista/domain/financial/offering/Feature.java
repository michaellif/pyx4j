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
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.offering.Feature.FeatureV;

@DiscriminatorValue("feature")
public interface Feature extends Product<FeatureV> {

    @I18n
    @XmlType(name = "FeatureType")
    public enum Type {

        parking(true),

        locker(true),

        pet(true),

        @Translate("Add-On")
        addOn(true),

        utility(true),

        booking(false);

        private final boolean inAgreement;

        Type(boolean inAgreement) {
            this.inAgreement = inAgreement;
        }

        public boolean isInAgreement() {
            return inAgreement;
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

    @ToStringFormat("{0}, {1}")
    @DiscriminatorValue("feature")
    public interface FeatureV extends Product.ProductV<Feature> {

        @NotNull
        @ToString(index = 0)
        IPrimitive<Type> featureType();

        IPrimitive<Boolean> recurring();

        IPrimitive<Boolean> mandatory();
    }

}
