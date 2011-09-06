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

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

@ToStringFormat("Type: {0}, Name: {1}")
public interface Feature extends IEntity {

    @Translatable
    public enum Type {

        parking(true),

        pet(true),

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

    @Translatable
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
    IList<ServiceItem> items();

    IPrimitive<PriceType> priceType();

    IPrimitive<DepositType> depositType();

    IPrimitive<Boolean> isRecurring();

    IPrimitive<Boolean> isMandatory();
}
