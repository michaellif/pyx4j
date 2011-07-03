/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-22
 * @author aroytbur
 * @version $Id$
 */
package com.propertyvista.domain.marketing.yield;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

@Deprecated
@ToStringFormat("{0} {1}")
public interface Concession extends IEntity {

    @Translatable
    public enum AppliedTo {

        full,

        monthly,

        amount,

        rent,

        parking,

        utilities;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @Translatable
    public enum Status {

        suggested,

        approved;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

// ----------------------------------------------

    @ToString(index = 0)
    @MemberColumn(name = "concessionType")
    IPrimitive<String> type();

    /**
     * Value discount
     */
    @ToString(index = 1)
    @Format("#0.00")
    @MemberColumn(name = "concessionValue")
    IPrimitive<Double> value();

    /**
     * Percent discount
     */
    IPrimitive<Double> percentage();

    IPrimitive<AppliedTo> appliedTo();

    IPrimitive<String> termType();

    /**
     * Number of terms to apply concession to
     */
    IPrimitive<Double> numberOfTerms();

    IPrimitive<String> description();

// ----------------------------------------------

    IPrimitive<Status> status();

    IPrimitive<String> approvedBy();

    @Caption(name = "Available From")
    @MemberColumn(name = "concessionStart")
    IPrimitive<LogicalDate> start();

    @Caption(name = "Available Till")
    @MemberColumn(name = "concessionEnd")
    IPrimitive<LogicalDate> end();
}
