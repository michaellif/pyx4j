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
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;
import com.propertyvista.domain.property.asset.unit.AptUnit;

@DiscriminatorValue("LeaseTermsPolicy")
@LowestApplicableNode(value = AptUnit.class)
public interface LeaseTermsPolicy extends Policy {

    @Owned
    IList<LegalTermsDescriptor> tenantSummaryTerms();

    @Owned
    IList<LegalTermsDescriptor> guarantorSummaryTerms();

    @Owned
    LegalTermsDescriptor oneTimePaymentTerms();

    @Owned
    LegalTermsDescriptor recurrentPaymentTerms();

}
