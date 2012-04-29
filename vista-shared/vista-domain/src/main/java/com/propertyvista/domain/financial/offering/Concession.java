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
package com.propertyvista.domain.financial.offering;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.offering.Concession.ConcessionV;

/**
 * Concessions needs to be versioned with service catalog.
 * if for concession instance within service_agreement effective and expiration dates are
 * required, we may want
 * to introduce the new interface under com.propertyvista.domain.tenant.lease called
 * LeaseConcession to align with
 * ProductCatalog.service.productItem -> ServiceAgreement.billableItem
 * ProductCatalog.concession -> ServiceAgreement.leaseConcession
 * 
 * @author Alex
 * 
 */

public interface Concession extends IVersionedEntity<ConcessionV> {

    @I18n(context = "Concession Type")
    @XmlType(name = "ConcessionType")
    enum Type {

        promotionalItem, percentageOff, monetaryOff, free;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n(context = "Concession Term")
    @XmlType(name = "ConcessionTerm")
    enum Term {

        firstMonth, lastMonth, term;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n(context = "Concession Condition")
    @XmlType(name = "ConcessionCondition")
    enum Condition {

        compliance, none;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

// ----------------------------------------------

    @Owner
    @NotNull
    @ReadOnly
    @Detached
    @JoinColumn
    ProductCatalog catalog();

    @OrderColumn
    IPrimitive<Integer> orderInCatalog();

    @Timestamp
    IPrimitive<Date> updated();

// ----------------------------------------------

    @ToStringFormat("{0}, {1}, ${2}")
    public interface ConcessionV extends IVersionData<Concession> {

        @NotNull
        @ToString(index = 0)
        @MemberColumn(name = "concessionType")
        IPrimitive<Type> type();

        @NotNull
        @ToString(index = 1)
        IPrimitive<Term> term();

        @Length(100)
        @Editor(type = Editor.EditorType.textarea)
        IPrimitive<String> description();

        /**
         * for free item - gift price
         * for percentageOff - percentage
         * for monetaryOff - amount
         * for skipFirstPayment - number of terms
         */
        @ToString(index = 2)
        @Format("#0.00")
        @MemberColumn(name = "val")
        IPrimitive<BigDecimal> value();

        @NotNull
        @MemberColumn(name = "cond")
        IPrimitive<Condition> condition();

        @NotNull
        ProductItemType productItemType();

        @NotNull
        IPrimitive<Integer> productItemQuantity();

        @NotNull
        IPrimitive<Boolean> nonMixable();

        @Format("MM/dd/yyyy")
        IPrimitive<LogicalDate> effectiveDate();

        @Format("MM/dd/yyyy")
        IPrimitive<LogicalDate> expirationDate();
    }
}
