/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2015
 * @author VladL
 */
package com.propertyvista.domain.policy.dto;

import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.policy.framework.PolicyDTOBase;
import com.propertyvista.domain.policy.policies.ApplicationApprovalChecklistPolicy;

@Transient
@ExtendsBO(ApplicationApprovalChecklistPolicy.class)
public interface ApplicationApprovalChecklistPolicyDTO extends PolicyDTOBase, ApplicationApprovalChecklistPolicy {

}
