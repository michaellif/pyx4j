/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared.dto.tenantinsurance;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.tenant.ptapp.DigitalSignature;
import com.propertyvista.dto.LegalTermsDescriptorDTO;

@Transient
public interface TenantSureCoverageRequestDTO extends IEntity {

    @NotNull
    IPrimitive<BigDecimal> personalLiabilityCoverage();

    @NotNull
    IPrimitive<BigDecimal> contentsCoverage();

    @Caption(name = "Deductible (per claim)")
    @NotNull
    IPrimitive<BigDecimal> deductible();

    // these are statement of fact questions
    @NotNull
    @Caption(name = "Number of Previous Claims")
    IPrimitive<Integer> numberOfPreviousClaims();

    @Caption(name = "Is any one of the tenants a smoker?")
    @NotNull
    IPrimitive<Boolean> smoker();

    IList<LegalTermsDescriptorDTO> personalDisclaimerTerms();

    IList<DigitalSignature> digitalSignatures();

}
