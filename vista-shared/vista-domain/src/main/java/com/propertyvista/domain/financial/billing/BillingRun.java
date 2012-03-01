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
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.domain.financial.billing;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.building.Building;

public interface BillingRun extends IEntity {

    @I18n
    enum BillingRunStatus {

        Scheduled,

        Running,

        Erred,

        Finished,

        Completed;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    @ReadOnly
    BillingCycle billingCycle();

    @ReadOnly
    IPrimitive<LogicalDate> billingPeriodStartDate();

    @ReadOnly
    IPrimitive<LogicalDate> billingPeriodEndDate();

    @ReadOnly
    Building building();

    IPrimitive<BillingRunStatus> status();

    @ReadOnly
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> executionTargetDate();

    @ReadOnly
    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> executionDate();

}
