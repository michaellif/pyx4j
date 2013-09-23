/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.domain.dto.financial.FinancialSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.BillingSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.TenantProfileSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceSummaryDTO;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface MainDashboardDTO extends IEntity {

    TenantProfileSummaryDTO profileInfo();

    MaintenanceSummaryDTO maintenanceInfo();

    BillingSummaryDTO billingSummary();

    @Deprecated
    FinancialSummaryDTO billSummary();

    @Deprecated
    IList<MaintenanceRequestDTO> maintanances();

    @Deprecated
    InsuranceStatusDTO tenantInsuranceStatus();
}
