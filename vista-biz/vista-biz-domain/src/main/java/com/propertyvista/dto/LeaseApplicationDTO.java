/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-12
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplicationStatus;

@Transient
@ExtendsBO(Lease.class)
public interface LeaseApplicationDTO extends LeaseDTO {

    MasterOnlineApplicationStatus masterApplicationStatus();

    IList<TenantFinancialDTO> tenantFinancials();

    LeaseApprovalDTO leaseApproval();

    IList<TenantInfoDTO> tenantInfo();

    IPrimitive<Integer> numberOfOccupants();

    IPrimitive<Integer> numberOfApplicants();

    IPrimitive<Integer> numberOfDepentands();

    IPrimitive<Integer> numberOfGuarantors();

    IList<LeaseApplicationDocument> applicationDocuments();

}
