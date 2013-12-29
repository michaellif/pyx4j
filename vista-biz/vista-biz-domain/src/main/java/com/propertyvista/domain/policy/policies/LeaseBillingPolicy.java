/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2012
 * @author igor
 * @version $Id$
 */

//TODO See VISTA-2605 for comments

package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.domain.policy.policies.domain.NsfFeeItem;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("LeaseBillingPolicy")
@LowestApplicableNode(value = Building.class)
public interface LeaseBillingPolicy extends Policy {

    @I18n
    enum BillConfirmationMethod {

        automatic, manual;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };
    }

    @Owned
    @OrderBy(LeaseBillingTypePolicyItem.OrderId.class)
    IList<LeaseBillingTypePolicyItem> availableBillingTypes();

    @NotNull
    IPrimitive<BillingAccount.ProrationMethod> prorationMethod();

    @Owned
    LateFeeItem lateFee();

    @Owned
    @OrderBy(NsfFeeItem.OrderId.class)
    IList<NsfFeeItem> nsfFees();

    @NotNull
    IPrimitive<BillConfirmationMethod> confirmationMethod();
}
