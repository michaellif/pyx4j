/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-21
 * @author jim
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.Summary;

@Transient
public interface SummaryDTO extends Summary {

    IList<ApartmentInfoSummaryDTO> apartmentSummary();

    ApartmentInfoDTO selectedUnit();

    TenantInApplicationListDTO tenantList();

    IList<TenantInfoDTO> tenantsWithInfo();

    IList<TenantFinancialDTO> tenantFinancials();

    Charges charges();
}
