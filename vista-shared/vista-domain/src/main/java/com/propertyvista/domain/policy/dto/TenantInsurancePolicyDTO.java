/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-13
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.domain.policy.dto;

import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.policy.framework.PolicyDTOBase;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;

@Transient
@ExtendsBO(TenantInsurancePolicy.class)
public interface TenantInsurancePolicyDTO extends TenantInsurancePolicy, PolicyDTOBase {

}
