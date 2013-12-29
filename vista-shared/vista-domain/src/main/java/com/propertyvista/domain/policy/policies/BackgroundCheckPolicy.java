/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 24, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("BackgroundCheckPolicy")
@LowestApplicableNode(value = Building.class)
public interface BackgroundCheckPolicy extends Policy {

    @XmlType(name = "BjccEntry")
    public enum BjccEntry {

        @Translate("12M")
        m12,

        @Translate("24M")
        m24,

        @Translate("36M")
        m36;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    interface BackgroundCheckPolicyV extends IEntity {

        @NotNull
        IPrimitive<BjccEntry> bankruptcy();

        @NotNull
        IPrimitive<BjccEntry> judgment();

        @NotNull
        IPrimitive<BjccEntry> collection();

        @NotNull
        IPrimitive<BjccEntry> chargeOff();

    }

    @Owned
    BackgroundCheckPolicy.BackgroundCheckPolicyV version();

    IPrimitive<Integer> strategyNumber();
}
