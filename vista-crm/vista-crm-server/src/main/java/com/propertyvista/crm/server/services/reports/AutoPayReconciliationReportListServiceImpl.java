/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 10, 2015
 * @author vlads
 */
package com.propertyvista.crm.server.services.reports;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.dto.reports.AutoPayReconciliationDTO;
import com.propertyvista.crm.rpc.services.reports.AutoPayReconciliationReportListService;
import com.propertyvista.crm.server.services.AbstractCrmCrudServiceImpl;
import com.propertyvista.crm.server.services.reports.calculators.AutoPayReconciliationCalculator;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.tenant.lease.Lease;

public class AutoPayReconciliationReportListServiceImpl extends AbstractCrmCrudServiceImpl<AutopayAgreement, AutoPayReconciliationDTO> implements
        AutoPayReconciliationReportListService {

    //TODO Secure this service

    public AutoPayReconciliationReportListServiceImpl() {
        super(AutopayAgreement.class, AutoPayReconciliationDTO.class);
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<AutopayAgreement> boCriteria, EntityListCriteria<AutoPayReconciliationDTO> toCriteria) {
        super.enhanceListCriteria(boCriteria, toCriteria);
        boCriteria.eq(boCriteria.proto().isDeleted(), false);
        // Only for active Leases. the same in AutopayManager.calulatePapAmounts
        boCriteria.in(boCriteria.proto().tenant().lease().status(), Lease.Status.active());
        // Active Buildings
        boCriteria.eq(boCriteria.proto().tenant().lease().unit().building().suspended(), false);
    }

    @Override
    protected void enhanceListRetrieved(AutopayAgreement bo, AutoPayReconciliationDTO to) {
        super.enhanceListRetrieved(bo, to);
        Persistence.ensureRetrieve(to.tenant().lease().unit().building(), AttachLevel.Attached);

        AutoPayReconciliationCalculator.calculate(to);
    }

}
