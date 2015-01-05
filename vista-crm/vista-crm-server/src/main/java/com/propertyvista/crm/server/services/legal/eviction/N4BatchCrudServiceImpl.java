/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 31, 2014
 * @author stanp
 */
package com.propertyvista.crm.server.services.legal.eviction;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.legal.InvoiceDebitAggregator;
import com.propertyvista.biz.legal.forms.n4.N4GenerationUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.crm.rpc.services.legal.eviction.N4BatchCrudService;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4BatchItem;
import com.propertyvista.domain.legal.n4.N4RentOwingForPeriod;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.N4BatchDTO;

public class N4BatchCrudServiceImpl extends AbstractCrudServiceDtoImpl<N4Batch, N4BatchDTO> implements N4BatchCrudService {

    private final N4Policy n4policy;

    public N4BatchCrudServiceImpl() {
        super(N4Batch.class, N4BatchDTO.class);
        n4policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class), N4Policy.class);
    }

    @Override
    protected N4BatchDTO init(InitializationData initializationData) {
        N4BatchDTO dto = EntityFactory.create(N4BatchDTO.class);

        dto.companyLegalName().setValue(n4policy.companyName().getValue());
        dto.companyAddress().set(n4policy.mailingAddress().duplicate(InternationalAddress.class));
        dto.companyPhoneNumber().setValue(n4policy.phoneNumber().getValue());
        dto.companyFaxNumber().setValue(n4policy.faxNumber().getValue());
        dto.companyEmailAddress().setValue(n4policy.emailAddress().getValue());

        for (Lease leaseId : ((N4BatchInitData) initializationData).leaseCandidates()) {
            N4BatchItem item = EntityFactory.create(N4BatchItem.class);

            List<N4RentOwingForPeriod> unpaidCharges = getUnpaidCharges(leaseId);
            BigDecimal amountOwed = BigDecimal.ZERO;
            for (N4RentOwingForPeriod rentOwingForPeriod : unpaidCharges) {
                amountOwed = amountOwed.add(rentOwingForPeriod.rentOwing().getValue());
            }
            item.rentOwingBreakdown().addAll(unpaidCharges);
            item.totalRentOwning().setValue(amountOwed);
            item.lease().set(leaseId);

            dto.items().add(item);
        }
        return dto;
    }

    @Override
    protected void enhanceRetrieved(N4Batch bo, N4BatchDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        Persistence.ensureRetrieve(to.items(), AttachLevel.Attached);
    }

    @Override
    protected void enhanceListRetrieved(N4Batch bo, N4BatchDTO to) {
        super.enhanceListRetrieved(bo, to);
    }

    // TODO - copied from SelectN4LeaseCandidateListServiceImpl; move to a facade
    private List<N4RentOwingForPeriod> getUnpaidCharges(Lease lease) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);

        HashSet<ARCode> acceptableArCodes = new HashSet<ARCode>(n4policy.relevantARCodes());
        LogicalDate today = SystemDateManager.getLogicalDate();

        List<InvoiceDebit> debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(lease.billingAccount());
        List<InvoiceDebit> filteredDebits = N4GenerationUtils.filterDebits(debits, acceptableArCodes, today);
        InvoiceDebitAggregator debitCalc = new InvoiceDebitAggregator();
        return debitCalc.debitsForPeriod(debitCalc.aggregate(filteredDebits));
    }
}
