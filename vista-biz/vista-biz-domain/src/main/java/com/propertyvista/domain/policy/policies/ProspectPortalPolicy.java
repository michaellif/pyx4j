/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("ProspectPortalPolicy")
@LowestApplicableNode(value = Building.class)
public interface ProspectPortalPolicy extends Policy {

    // Unit Selection Rules:
    @Caption(description = "How old available unit to display (in days)")
    IPrimitive<Integer> unitAvailabilitySpan();

    @Caption(description = "How many units to show in Exact Match list")
    IPrimitive<Integer> maxExactMatchUnits();

    @Caption(description = "How many units to show in Partial Match list")
    IPrimitive<Integer> maxPartialMatchUnits();

    // Application Payment Rules:

    @I18n(context = "Fee Payment")
    @XmlType(name = "FeePayment")
    public enum FeePayment {

        perLease,

        perApplicant,

        none;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    @Editor(type = EditorType.combo)
    @Caption(description = "Type of fee definition")
    IPrimitive<FeePayment> feePayment();

    @Format("#,##0.00")
    @Caption(description = "Amount of fee definition")
    IPrimitive<BigDecimal> feeAmount();
}
