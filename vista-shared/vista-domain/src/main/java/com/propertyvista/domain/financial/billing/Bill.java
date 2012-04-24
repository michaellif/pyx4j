/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.billing;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.BillingAccount;

@Table(prefix = "billing")
public interface Bill extends IEntity {

    @I18n
    enum BillStatus {

        Running,

        Failed,

        Finished,

        Confirmed,

        Rejected;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    @I18n
    enum BillType {

        First,

        Regular,

        Final;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    @Owner
    @NotNull
    @JoinColumn
    @Detached
    BillingAccount billingAccount();

    IPrimitive<Integer> billSequenceNumber();

    /**
     * If draft is true no need to verify it. Next bill will run on the same billing cycle.
     */
    IPrimitive<Boolean> draft();

    IPrimitiveSet<String> warnings();

    Bill previousBill();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> billingPeriodStartDate();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> billingPeriodEndDate();

    IPrimitive<BillStatus> billStatus();

    IPrimitive<BillType> billType();

    @Length(50)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> rejectReason();

    @ReadOnly
    BillingRun billingRun();

    @EmbeddedEntity
    Invoice invoice();

}
