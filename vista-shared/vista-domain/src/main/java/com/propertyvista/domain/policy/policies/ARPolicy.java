/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.property.asset.building.Building;

@Caption(name = "Aging Priority")
@DiscriminatorValue("ARPolicy")
@LowestApplicableNode(value = Building.class)
public interface ARPolicy extends Policy {

    @I18n
    enum CreditDebitRule {
        @Translate("Rent Debt Last")
        rentDebtLast,

        @Translate("Oldest Debt First")
        oldestDebtFirst;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };
    }

    @Caption(description = "The rule by which the automatic credit-debit links will be created.")
    @NotNull
    IPrimitive<CreditDebitRule> creditDebitRule();
}
