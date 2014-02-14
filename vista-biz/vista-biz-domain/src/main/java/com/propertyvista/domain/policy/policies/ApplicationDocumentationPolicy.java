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
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;

@DiscriminatorValue("ApplicationDocumentationPolicy")
public interface ApplicationDocumentationPolicy extends Policy, TenantsAccessiblePolicy {

    @NotNull
    @Caption(description = "The number of the IDs that is required for an application")
    IPrimitive<Integer> numberOfRequiredIDs();

    @Owned
    @NotNull
    @Caption(description = "IDs/Documentations that accepted as valid IDs")
    IList<IdentificationDocumentType> allowedIDs();

    @NotNull
    @Editor(type = EditorType.radiogroup)
    @Caption(description = "Is the proof of income documents are mandatory")
    IPrimitive<Boolean> mandatoryProofOfIncome();
}
