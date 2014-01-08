/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;
import com.propertyvista.domain.property.asset.unit.AptUnit;

@DiscriminatorValue("LegalDocumentationPolicy")
@LowestApplicableNode(value = AptUnit.class)
public interface PmcTermsPolicy extends Policy, TenantsAccessiblePolicy {

    @Owned
    //@Length(20845)
    @Editor(type = Editor.EditorType.richtextarea)
    //TODO Blob
    IPrimitive<String> rentalCriteriaGuidelines();

    @Owned
    IList<LegalTermsDescriptor> mainApplication();

    @Owned
    IList<LegalTermsDescriptor> coApplication();

    @Owned
    IList<LegalTermsDescriptor> guarantorApplication();

    @Owned
    IList<LegalTermsDescriptor> lease();

    @Owned
    IList<LegalTermsDescriptor> paymentAuthorization();
}
