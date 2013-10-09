/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.web.dto.insurance;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;

@Transient
/** Holds input options */
public interface TenantSureAgreementParamsDTO extends IEntity {

    IPrimitiveSet<BigDecimal> generalLiabilityCoverageOptions();

    IPrimitiveSet<BigDecimal> contentsCoverageOptions();

    IPrimitiveSet<BigDecimal> deductibleOptions();

    IPrimitive<String> preAuthorizedDebitAgreement();

    /**
     * @returns <code>true</code> if the tenant in context has already been registered as a client registered in CFC API (which means we don't need to validate
     *          name and phone), or <code>false</code> otherwise.
     */
    IPrimitive<Boolean> isTenantInitializedInCfc();
}
