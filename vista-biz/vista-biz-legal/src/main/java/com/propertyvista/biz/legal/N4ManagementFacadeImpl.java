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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.LegalNoticeCandidate;
import com.propertyvista.domain.legal.N4FormFieldsData;
import com.propertyvista.domain.legal.N4LandlordsData;
import com.propertyvista.domain.legal.N4LeaseData;
import com.propertyvista.domain.legal.N4LegalLetter;
import com.propertyvista.domain.legal.N4RentOwingForPeriod;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.domain.LegalLetterBlob;

public class N4ManagementFacadeImpl implements N4ManagementFacade {

    @Override
    public List<LegalNoticeCandidate> getN4Candidates(BigDecimal minAmountOwed, List<Building> buildingIds) {
        if (minAmountOwed == null) {
            minAmountOwed = BigDecimal.ZERO;
        }

        N4Policy n4policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class),
                N4Policy.class);
        HashSet<ARCode> acceptableArCodes = new HashSet<ARCode>(n4policy.relevantArCodes());

        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.in(criteria.proto().status(), Lease.Status.active());
        if (buildingIds != null && !buildingIds.isEmpty()) {
            criteria.in(criteria.proto().unit().building(), buildingIds);
        }

        // TODO not sure that sorting should be done in this method
        criteria.asc(criteria.proto().unit().building().propertyCode());
        criteria.asc(criteria.proto().unit().info().number());

        List<Lease> leases = Persistence.service().query(criteria);

        List<LegalNoticeCandidate> candidates = new LinkedList<LegalNoticeCandidate>();
        for (Lease lease : leases) {

            BigDecimal amountOwed = amountOwed(lease.billingAccount(), acceptableArCodes);

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
    public void issueN4(List<Lease> delinquentLeases, Employee employee, LogicalDate noticeDate, AtomicInteger progress) throws IllegalStateException {

        N4Policy n4policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class),
                N4Policy.class);

        N4LandlordsData n4LandLordsData = EntityFactory.create(N4LandlordsData.class);
        {
            n4LandLordsData.landlordsLegalName().setValue(n4policy.companyName().getValue());
            n4LandLordsData.signingEmployee().set(Persistence.service().retrieve(Employee.class, employee.getPrimaryKey()));
            n4LandLordsData.landlordsAddress().set(n4policy.mailingAddress());
            n4LandLordsData.landlordsPhoneNumber().setValue(n4policy.phoneNumber().getValue());
            n4LandLordsData.faxNumber().setValue(n4policy.faxNumber().getValue());
            n4LandLordsData.emailAddress().setValue(n4policy.emailAddress().getValue());
            n4LandLordsData.isLandlord().setValue(false);
            n4LandLordsData.signatureDate().setValue(new LogicalDate(SystemDateManager.getDate()));

            // TODO fill signature from employees data
            n4LandLordsData.signature();
        }

        for (Lease leaseId : delinquentLeases) {
            issueN4ForLease(leaseId, n4LandLordsData, noticeDate, new HashSet<ARCode>(n4policy.relevantArCodes()));
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

    private void issueN4ForLease(Lease leaseId, N4LandlordsData n4LandLordsData, LogicalDate noticeDate, Collection<ARCode> relevantArCodes) {

        N4LeaseData n4LeaseData = ServerSideFactory.create(N4GenerationFacade.class).prepareN4LeaseData(leaseId, noticeDate, relevantArCodes);
        N4FormFieldsData n4FormData = ServerSideFactory.create(N4GenerationFacade.class).populateFormData(n4LeaseData, n4LandLordsData);
        byte[] n4LetterBinary = ServerSideFactory.create(N4GenerationFacade.class).generateN4Letter(n4FormData);

        LegalLetterBlob blob = EntityFactory.create(LegalLetterBlob.class);
        blob.content().setValue(n4LetterBinary);
        blob.contentType().setValue("application/pdf");
        Persistence.service().persist(blob);

        N4LegalLetter n4Letter = EntityFactory.create(N4LegalLetter.class);
        n4Letter.lease().set(leaseId);
        n4Letter.amountOwed().setValue(n4LeaseData.totalRentOwning().getValue());
        n4Letter.generatedOn().setValue(SystemDateManager.getDate());
        n4Letter.blobKey().setValue(blob.getPrimaryKey());
        n4Letter.fileSize().setValue(n4LetterBinary.length);
        n4Letter.fileName().setValue(MessageFormat.format("n4notice-{0,date,yyyy-MM-dd}.pdf", SystemDateManager.getDate()));
        Persistence.service().persist(n4Letter);
    }

    private BigDecimal amountOwed(BillingAccount billingAccount, Collection<ARCode> acceptableArCodes) {
        List<InvoiceDebit> debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(billingAccount);
        List<InvoiceDebit> filteredDebits = new ArrayList<InvoiceDebit>(debits.size());
        for (InvoiceDebit debit : debits) {
            if (acceptableArCodes.contains(debit.arCode())) {
                filteredDebits.add(debit);
            }
        }
        InvoiceDebitAggregator debitAggregator = new InvoiceDebitAggregator();
        List<N4RentOwingForPeriod> rentOwingBreakdown = debitAggregator.debitsForPeriod(debitAggregator.aggregate(filteredDebits));

        BigDecimal amountOwed = BigDecimal.ZERO;
        for (N4RentOwingForPeriod rentOwingForPeriod : rentOwingBreakdown) {
            amountOwed = amountOwed.add(rentOwingForPeriod.rentOwing().getValue());
        }
        return amountOwed;
    }

}
