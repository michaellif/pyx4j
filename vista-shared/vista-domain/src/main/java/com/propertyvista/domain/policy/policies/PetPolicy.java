/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 29, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.policies.domain.PetConstraints;
import com.propertyvista.domain.property.asset.unit.AptUnit;

@DiscriminatorValue("PetPolicy")
@LowestApplicableNode(value = AptUnit.class)
public interface PetPolicy extends Policy, TenantsAccessiblePolicy {

    @MemberColumn(name = "petConstraints")
    @Owned
    IList<PetConstraints> constraints();

    /** This is required only to let the persistence engine to create a table */
    @Deprecated
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    IPrimitive<Integer> x();
}
