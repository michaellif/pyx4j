/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 30, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.offeringnew;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

public interface Concession extends IEntity {

    @Translatable
    enum Type {
        promotionalItem, percentageOff, monetaryOff, free;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    enum Term {
        firstMonth, lastMonth, term;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    enum Condition {
        compliance, none;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    public enum Status {

        draft,

        approved,

        denied;

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

    @ToString(index = 0)
    @MemberColumn(name = "concessionType")
    IPrimitive<Type> type();

    IPrimitive<Term> term();

    IPrimitive<String> description();

    /**
     * for free item - gift price
     * for percentageOff - percentage
     * for monetaryOff - amount
     * for skipFirstPayment - number of terms
     */
    @ToString(index = 1)
    @Format("#0.00")
    @MemberColumn(name = "concessionValue")
    IPrimitive<Double> value();

    @MemberColumn(name = "concessionondition")
    IPrimitive<Condition> condition();

// ----------------------------------------------

    IPrimitive<Status> status();

    IPrimitive<String> approvedBy();

    @Caption(name = "Effective Date")
    @MemberColumn(name = "effectiveDate")
    IPrimitive<LogicalDate> effectiveDate();

    @Caption(name = "Expiration Date")
    @MemberColumn(name = "expirationDate")
    IPrimitive<LogicalDate> expirationDate();
}
