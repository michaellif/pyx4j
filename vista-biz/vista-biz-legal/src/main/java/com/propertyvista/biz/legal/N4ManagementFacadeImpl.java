/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.legal.forms.n4.N4GenerationUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.domain.blob.EmployeeSignatureBlob;
import com.propertyvista.domain.blob.LegalLetterBlob;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.EmployeeSignature;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.LegalNoticeCandidate;
import com.propertyvista.domain.legal.ltbcommon.RentOwingForPeriod;
import com.propertyvista.domain.legal.n4.N4BatchData;
import com.propertyvista.domain.legal.n4.N4FormFieldsData;
import com.propertyvista.domain.legal.n4.N4LeaseData;
import com.propertyvista.domain.legal.n4.N4LegalLetter;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class N4ManagementFacadeImpl implements N4ManagementFacade {

    private static final Logger log = LoggerFactory.getLogger(N4ManagementFacadeImpl.class);

    @Override
    public List<LegalNoticeCandidate> getN4Candidates(BigDecimal minAmountOwed, List<Building> buildingIds) {
        if (minAmountOwed == null) {
            minAmountOwed = BigDecimal.ZERO;
        }

        N4Policy n4policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class),
                N4Policy.class);
        HashSet<ARCode> acceptableArCodes = new HashSet<ARCode>(n4policy.relevantARCodes());

        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.in(criteria.proto().status(), Lease.Status.active());
        if (buildingIds != null && !buildingIds.isEmpty()) {
            criteria.in(criteria.proto().unit().building(), buildingIds);
        }

        // TODO not sure that sorting should be done in this method
        criteria.asc(criteria.proto().unit().building().propertyCode());
        criteria.asc(criteria.proto().unit().info().number());

        List<Lease> leases = Persistence.secureQuery(criteria);

        List<LegalNoticeCandidate> candidates = new LinkedList<LegalNoticeCandidate>();
        LogicalDate today = SystemDateManager.getLogicalDate();
        for (Lease lease : leases) {
            BigDecimal amountOwed = amountOwed(lease.billingAccount(), acceptableArCodes, today);

            if (amountOwed.compareTo(minAmountOwed) > 0) {
                LegalNoticeCandidate candidate = EntityFactory.create(LegalNoticeCandidate.class);
                candidate.leaseId().set(lease.createIdentityStub());
                candidate.amountOwed().setValue(amountOwed);
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    @Override
    public void issueN4(N4BatchRequestDTO batchRequest, AtomicInteger progress) throws IllegalStateException {

        N4BatchData batchData = EntityFactory.create(N4BatchData.class);
        batchData.noticeDate().setValue(batchRequest.noticeDate().getValue());
        batchData.deliveryMethod().setValue(batchRequest.deliveryMethod().getValue());
        batchData.buildingOwnerLegalName().setValue(batchRequest.buildingOwnerName().getValue());
        batchData.buildingOwnerAddress().set(batchRequest.buildingOwnerMailingAddress());
        batchData.companyLegalName().setValue(batchRequest.companyName().getValue());
        batchData.signingEmployee().set(Persistence.service().retrieve(Employee.class, batchRequest.agent().getPrimaryKey()));
        batchData.companyAddress().set(batchRequest.mailingAddress());

        batchData.companyPhoneNumber().setValue(batchRequest.phoneNumber().getValue());
        batchData.companyFaxNumber().setValue(batchRequest.faxNumber().getValue());
        batchData.companyEmailAddress().setValue(batchRequest.emailAddress().getValue());

        batchData.isLandlord().setValue(false); // TODO right now we always assume it's agent
        batchData.signatureDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        batchData.signature().setValue(retrieveSignature(batchData.signingEmployee()));

        // TODO fix this: policy should be applied on lease level, right now n4 policy can be set up for Organization so it should be fine        
        N4Policy n4policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class),
                N4Policy.class);

        Date batchGenerationDate = SystemDateManager.getDate();
        for (Lease leaseId : batchRequest.targetDelinquentLeases()) {
            generateN4ForLease(leaseId, batchData, new HashSet<ARCode>(n4policy.relevantARCodes()), batchGenerationDate);
            progress.set(progress.get() + 1);
        }

    }

    @Override
    public Map<Lease, List<N4LegalLetter>> getN4(List<Lease> leaseIds, LogicalDate generatedCutOffDate) {
        Map<Lease, List<N4LegalLetter>> n4s = new HashMap<Lease, List<N4LegalLetter>>();
        for (Lease leaseId : leaseIds) {
            EntityQueryCriteria<N4LegalLetter> criteria = EntityQueryCriteria.create(N4LegalLetter.class);
            criteria.eq(criteria.proto().lease(), leaseId);
            if (generatedCutOffDate != null) {
                criteria.ge(criteria.proto().generatedOn(), generatedCutOffDate);
            }
            criteria.asc(criteria.proto().generatedOn());

            List<N4LegalLetter> letters = Persistence.service().query(criteria);

            n4s.put(leaseId, letters);
        }
        return n4s;
    }

    private void generateN4ForLease(Lease leaseId, N4BatchData batchData, Collection<ARCode> relevantArCodes, Date generationTime) {
        try {
            N4LeaseData n4LeaseData = ServerSideFactory.create(N4GenerationFacade.class).prepareN4LeaseData(leaseId, batchData.noticeDate().getValue(),
                    batchData.deliveryMethod().getValue(), relevantArCodes);
            N4FormFieldsData n4FormData = ServerSideFactory.create(N4GenerationFacade.class).prepareFormData(n4LeaseData, batchData);
            byte[] n4LetterBinary = ServerSideFactory.create(N4GenerationFacade.class).generateN4Letter(n4FormData);

            LegalLetterBlob blob = EntityFactory.create(LegalLetterBlob.class);
            blob.data().setValue(n4LetterBinary);
            blob.contentType().setValue("application/pdf");
            Persistence.service().persist(blob);

            N4LegalLetter n4Letter = EntityFactory.create(N4LegalLetter.class);
            n4Letter.lease().set(leaseId);
            n4Letter.amountOwed().setValue(n4LeaseData.totalRentOwning().getValue());
            n4Letter.generatedOn().setValue(generationTime);
            n4Letter.file().blobKey().setValue(blob.getPrimaryKey());
            n4Letter.file().fileSize().setValue(n4LetterBinary.length);
            n4Letter.file().fileName().setValue(MessageFormat.format("n4notice-{0,date,yyyy-MM-dd}.pdf", generationTime));
            Persistence.service().persist(n4Letter);
        } catch (Throwable error) {
            log.error("Failed to generate n4 for lease pk='" + leaseId.getPrimaryKey() + "'", error);

            throw new RuntimeException(error);
        }
    }

    private BigDecimal amountOwed(BillingAccount billingAccount, Collection<ARCode> acceptableArCodes, LogicalDate asOf) {
        List<InvoiceDebit> debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(billingAccount);
        List<InvoiceDebit> filteredDebits = N4GenerationUtils.filterDebits(debits, acceptableArCodes, asOf);
        InvoiceDebitAggregator debitAggregator = new InvoiceDebitAggregator();
        List<RentOwingForPeriod> rentOwingBreakdown = debitAggregator.debitsForPeriod(debitAggregator.aggregate(filteredDebits));

        BigDecimal amountOwed = BigDecimal.ZERO;
        for (RentOwingForPeriod rentOwingForPeriod : rentOwingBreakdown) {
            amountOwed = amountOwed.add(rentOwingForPeriod.rentOwing().getValue());
        }
        return amountOwed;
    }

    /** Retrieves Employee's signature image from the db or returns <code>null</code> if the employee hasn't uploaded a signature image */
    private byte[] retrieveSignature(Employee signingEmployee) {
        if (!signingEmployee.signature().isNull()) {
            EmployeeSignature signature = Persistence.service().retrieve(EmployeeSignature.class, signingEmployee.signature().getPrimaryKey());
            EmployeeSignatureBlob signatureBlob = Persistence.service().retrieve(EmployeeSignatureBlob.class, signature.file().blobKey().getValue());
            return signatureBlob.data().getValue();
        } else {
            return null;
        }
    }

}
