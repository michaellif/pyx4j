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

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@ToStringFormat("Type: {0}, Name: {1}")
public interface Feature extends IEntity {

    @I18n
    @XmlType(name = "FeatureType")
    public enum Type {

        parking(true),

        pet(true),

        @Translate("Add-On")
        addOn(true),

        utility(true),

        locker(true),

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
            return I18nEnum.tr(this);
        }
    }

    @I18n
    enum PriceType {
        percentageFromServicePrice, fixed;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

// ----------------------------------------------

    @Owner
    @Detached
    @ReadOnly
    ServiceCatalog catalog();

// ----------------------------------------------

    @NotNull
    @ToString(index = 0)
    @MemberColumn(name = "featureType")
    IPrimitive<Type> type();

    @ToString(index = 1)
    IPrimitive<String> name();

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @Owned
    @Detached
    IList<ServiceItem> items();

    IPrimitive<PriceType> priceType();

    IPrimitive<DepositType> depositType();

    IPrimitive<Boolean> isRecurring();

    IPrimitive<Boolean> isMandatory();
}
