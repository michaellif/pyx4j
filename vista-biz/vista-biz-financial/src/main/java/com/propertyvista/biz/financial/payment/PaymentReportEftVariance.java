/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.payment.AutopaytManager.PreauthorizedAmount;
import com.propertyvista.crm.rpc.dto.reports.EftVarianceReportRecordDTO;
import com.propertyvista.crm.rpc.dto.reports.EftVarianceReportRecordDetailsDTO;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;

class PaymentReportEftVariance {

    public List<EftVarianceReportRecordDTO> reportEftVariance(PreauthorizedPaymentsReportCriteria reportCriteria) {
        List<EftVarianceReportRecordDTO> records = new ArrayList<EftVarianceReportRecordDTO>();

        ICursorIterator<BillingAccount> billingAccountIterator;
        { //TODO->Closure
            EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
            if (reportCriteria.selectedBuildings != null) {
                criteria.in(criteria.proto().lease().unit().building(), reportCriteria.selectedBuildings);
            }
            criteria.eq(criteria.proto().lease().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments().$().isDeleted(), false);

            if (reportCriteria.isLeasesOnNoticeOnly()) {
                criteria.eq(criteria.proto().lease().completion(), Lease.CompletionType.Notice);
            }

            if (reportCriteria.hasExpectedMoveOutFilter()) {
                criteria.ge(criteria.proto().lease().expectedMoveOut(), reportCriteria.getMinExpectedMoveOut());
                criteria.le(criteria.proto().lease().expectedMoveOut(), reportCriteria.getMaxExpectedMoveOut());
            }

            criteria.asc(criteria.proto().lease().unit().building().propertyCode());
            criteria.asc(criteria.proto().lease().leaseId());

            billingAccountIterator = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
        }
        try {
            while (billingAccountIterator.hasNext()) {
                EftVarianceReportRecordDTO leaseRecord = createEftVarianceRecord(billingAccountIterator.next());
                if (leaseRecord != null) {
                    records.add(leaseRecord);
                }
            }
        } finally {
            billingAccountIterator.close();
        }

        return records;
    }

    private EftVarianceReportRecordDTO createEftVarianceRecord(BillingAccount billingAccount) {
        Persistence.ensureRetrieve(billingAccount.lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(billingAccount.lease().unit(), AttachLevel.Attached);
        Persistence.ensureRetrieve(billingAccount.lease().unit().building(), AttachLevel.Attached);

        BillingCycle billingCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(billingAccount.lease());
        List<PreauthorizedAmount> preauthorizedRecords = new AutopaytManager().calulatePapAmounts(billingCycle, billingAccount);
        if (preauthorizedRecords.size() == 0) {
            return null;
        }

        EftVarianceReportRecordDTO leaseRecord = EntityFactory.create(EftVarianceReportRecordDTO.class);
        leaseRecord.building().setValue(billingAccount.lease().unit().building().propertyCode().getValue());
        leaseRecord.unit().setValue(billingAccount.lease().unit().info().number().getValue());
        leaseRecord.leaseId().setValue(billingAccount.lease().leaseId().getValue());
        leaseRecord.leaseId_().set(billingAccount.lease().createIdentityStub());

        leaseRecord.leaseTotals().totalEft().setValue(BigDecimal.ZERO);
        leaseRecord.leaseTotals().charges().setValue(BigDecimal.ZERO);

        // Add each payment record
        for (PreauthorizedAmount record : preauthorizedRecords) {
            EftVarianceReportRecordDetailsDTO detail = EntityFactory.create(EftVarianceReportRecordDetailsDTO.class);

            Persistence.ensureRetrieve(record.preauthorizedPayment.tenant(), AttachLevel.Attached);
            detail.tenantName().setValue(record.preauthorizedPayment.tenant().customer().person().name().getStringView());

            detail.paymentMethod().setValue(record.preauthorizedPayment.paymentMethod().getStringView());

            detail.totalEft().setValue(record.amount);
            leaseRecord.leaseTotals().totalEft().setValue(leaseRecord.leaseTotals().totalEft().getValue().add(record.amount));
            leaseRecord.details().add(detail);
        }

        // Calculate charges
        Map<String, BillableItem> billableItems = PaymentBillableUtils.getAllBillableItems(billingAccount.lease().currentTerm().version());
        for (BillableItem billableItem : billableItems.values()) {
            leaseRecord.leaseTotals().charges().setValue(leaseRecord.leaseTotals().charges().getValue().add(PaymentBillableUtils.getActualPrice(billableItem)));
        }

        leaseRecord.leaseTotals().difference()
                .setValue(leaseRecord.leaseTotals().totalEft().getValue().subtract(leaseRecord.leaseTotals().charges().getValue()));
        return leaseRecord;
    }
}
