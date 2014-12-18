/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 10, 2014
 * @author vlads
 */
package com.propertyvista.domain.tenant.insurance;

import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

/**
 * This is the data entered by tenant and is used to receive Quote
 * This fragment of data is not copied to TenantSureInsurancePolicy or certificate
 */
public interface TenantSureCoverage extends IEntity {

    // -- Coverage Qualification Questions

    @BusinessEqualValue
    IPrimitive<Integer> previousClaims();

    @NotNull
    @BusinessEqualValue
    @Caption(name = "Is any tenant a smoker?")
    IPrimitive<Boolean> smoker();
}
