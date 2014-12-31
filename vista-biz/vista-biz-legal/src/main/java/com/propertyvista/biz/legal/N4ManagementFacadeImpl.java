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
 */
package com.propertyvista.biz.legal;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.Pair;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.legal.forms.n4.N4GenerationUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.domain.blob.EmployeeSignatureBlob;
import com.propertyvista.domain.blob.LegalLetterBlob;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.EmployeeSignature;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.legal.LegalNoticeCandidate;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.domain.legal.LegalStatus.Status;
import com.propertyvista.domain.legal.LegalStatusN4;
import com.propertyvista.domain.legal.errors.FormFillError;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4BatchItem;
import com.propertyvista.domain.legal.n4.N4FormFieldsData;
import com.propertyvista.domain.legal.n4.N4LegalLetter;
import com.propertyvista.domain.legal.n4.N4RentOwingForPeriod;
import com.propertyvista.domain.legal.n4cs.N4CSFormFieldsData;
import com.propertyvista.domain.legal.n4cs.N4CSServiceMethod.ServiceMethod;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.lease.Lease;

public class N4ManagementFacadeImpl implements N4ManagementFacade {

    private static final Logger log = LoggerFactory.getLogger(N4ManagementFacadeImpl.class);

    @Override
    public List<LegalNoticeCandidate> getN4Candidates(BigDecimal minAmountOwed, List<Building> buildingIds, ExecutionMonitor executionMonitor) {
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

        executionMonitor.setExpectedTotal((long) Persistence.service().count(criteria));

        ICursorIterator<Lease> leases = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
        try {
            List<LegalNoticeCandidate> candidates = new LinkedList<LegalNoticeCandidate>();
            LogicalDate today = SystemDateManager.getLogicalDate();
            while (leases.hasNext() && !executionMonitor.isTerminationRequested()) {
                Lease lease = leases.next();
                BigDecimal amountOwed = amountOwed(lease.billingAccount(), acceptableArCodes, today);
                boolean hasActiveN4 = hasLegalStatus(lease);

                if (!hasActiveN4 && amountOwed.compareTo(minAmountOwed) > 0) {
                    LegalNoticeCandidate candidate = EntityFactory.create(LegalNoticeCandidate.class);
                    candidate.leaseId().set(lease.createIdentityStub());
                    candidate.amountOwed().setValue(amountOwed);
                    candidates.add(candidate);
                }
                executionMonitor.addProcessedEvent("Check lease for N4");
            }

            return candidates;
        } finally {
            IOUtils.closeQuietly(leases);
        }

    }

    @Override
    public List<Pair<Lease, Exception>> issueN4(final N4BatchRequestDTO batchRequest, AtomicInteger progress) throws IllegalStateException, FormFillError {
        // TODO fix this: policy should be applied on lease level, right now n4 policy can be set up for Organization so it should be fine
        N4Policy n4policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class),
                N4Policy.class);
        final Collection<ARCode> relevantArCodes = new HashSet<ARCode>(n4policy.relevantARCodes());
        final Date batchGenerationDate = SystemDateManager.getDate();

        List<Pair<Lease, Exception>> failed = new LinkedList<>();
        final N4Batch batch = makeBatchData(batchRequest);
        for (final Lease leaseId : batchRequest.targetDelinquentLeases()) {
            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.Web).execute(new Executable<Void, Exception>() {
                    @Override
                    public Void execute() throws Exception {
                        issueN4ForLease(leaseId, batch, relevantArCodes, batchGenerationDate);
                        return null;
                    }
                });
            } catch (Exception e) {
                log.error("Failed to generate n4 for lease pk='" + leaseId.getPrimaryKey() + "'", e);
                failed.add(new Pair<>(leaseId, e));
            }
            progress.set(progress.get() + 1);
        }
        Persistence.service().persist(batch);
        return failed;
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

    private void issueN4ForLease(Lease leaseId, N4Batch batchData, Collection<ARCode> relevantArCodes, Date generationTime) throws FormFillError {
        N4BatchItem n4LeaseData = (N4BatchItem) ServerSideFactory.create(N4GenerationFacade.class).prepareN4LeaseData(leaseId,
                batchData.noticeDate().getValue(), batchData.deliveryMethod().getValue(), relevantArCodes);
        batchData.items().add(n4LeaseData);
        if (false) {
            N4FormFieldsData n4FormData = ServerSideFactory.create(N4GenerationFacade.class).prepareFormData(n4LeaseData, batchData);
            N4CSFormFieldsData n4csFormData = ServerSideFactory.create(N4CSGenerationFacade.class).prepareN4CSData(n4FormData, ServiceMethod.M);

            byte[] n4LetterBinary = ServerSideFactory.create(N4GenerationFacade.class).generateN4Letter(n4FormData);
            byte[] n4csLetterBinary = ServerSideFactory.create(N4CSGenerationFacade.class).generateN4CSLetter(n4csFormData);

            LegalLetterBlob blob = EntityFactory.create(LegalLetterBlob.class);
            blob.data().setValue(n4LetterBinary);
            blob.contentType().setValue("application/pdf");
            Persistence.service().persist(blob);
        }
        N4LegalLetter n4Letter = EntityFactory.create(N4LegalLetter.class);
        n4Letter.lease().set(leaseId);
        n4Letter.amountOwed().setValue(n4LeaseData.totalRentOwning().getValue());
        n4Letter.terminationDate().setValue(n4LeaseData.terminationDate().getValue());
        n4Letter.generatedOn().setValue(generationTime);

        // n4Letter.file().blobKey().setValue(blob.getPrimaryKey());
        // n4Letter.file().fileSize().setValue(n4LetterBinary.length);
        n4Letter.file().fileName().setValue(MessageFormat.format("n4-notice-{0,date,yyyy-MM-dd}.pdf", generationTime));

        Persistence.service().persist(n4Letter);

        LegalStatusN4 n4Status = EntityFactory.create(LegalStatusN4.class);
        n4Status.status().setValue(Status.N4);

        N4Policy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(unit(leaseId), N4Policy.class);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(generationTime);
        cal.add(GregorianCalendar.DAY_OF_YEAR, policy.expiryDays().getValue());
        n4Status.expiry().setValue(cal.getTime());
        n4Status.cancellationThreshold().setValue(policy.cancellationThreshold().getValue());
        n4Status.terminationDate().setValue(n4LeaseData.terminationDate().getValue());

        n4Status.notes().setValue("created via N4 notice batch");
        n4Status.setBy().set(EntityFactory.createIdentityStub(CrmUser.class, VistaContext.getCurrentUserPrimaryKey()));
        n4Status.setOn().setValue(generationTime);

        ServerSideFactory.create(LeaseLegalFacade.class).setLegalStatus(//@formatter:off
                    leaseId,
                    n4Status,
                    Arrays.<LegalLetter>asList(n4Letter)
            );//@formatter:on

        /* The same steps to write certificate stream to the database and create corresponding records for further use */

        LegalLetterBlob csBlob = EntityFactory.create(LegalLetterBlob.class);
//        csBlob.data().setValue(n4csLetterBinary);
        csBlob.contentType().setValue("application/pdf");
        Persistence.service().persist(csBlob);

        N4LegalLetter n4csLetter = EntityFactory.create(N4LegalLetter.class);
        n4csLetter.lease().set(leaseId);
        n4csLetter.amountOwed().setValue(n4LeaseData.totalRentOwning().getValue());
        n4csLetter.terminationDate().setValue(n4LeaseData.terminationDate().getValue());
        n4csLetter.generatedOn().setValue(generationTime);

        n4csLetter.file().blobKey().setValue(csBlob.getPrimaryKey());
//        n4csLetter.file().fileSize().setValue(n4csLetterBinary.length);
        n4csLetter.file().fileName().setValue(MessageFormat.format("n4-notice-certificate-{0,date,yyyy-MM-dd}.pdf", generationTime));

        Persistence.service().persist(n4csLetter);
//TODO: Change status
        LegalStatusN4 n4csStatus = EntityFactory.create(LegalStatusN4.class);
        n4csStatus.status().setValue(Status.N4CS);// Should be changed to N4CS

        N4Policy csPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(unit(leaseId), N4Policy.class);
        cal = new GregorianCalendar();
        cal.setTime(generationTime);
        cal.add(GregorianCalendar.DAY_OF_YEAR, csPolicy.expiryDays().getValue());
        n4csStatus.expiry().setValue(cal.getTime());
        n4csStatus.cancellationThreshold().setValue(csPolicy.cancellationThreshold().getValue());
        n4csStatus.terminationDate().setValue(n4LeaseData.terminationDate().getValue());

        n4csStatus.notes().setValue("created via N4 notice batch");
        n4csStatus.setBy().set(EntityFactory.createIdentityStub(CrmUser.class, VistaContext.getCurrentUserPrimaryKey()));
        n4csStatus.setOn().setValue(generationTime);

        ServerSideFactory.create(LeaseLegalFacade.class).setLegalStatus(//@formatter:off
                    leaseId,
                    n4csStatus,
                    Arrays.<LegalLetter>asList(n4csLetter)
            );//@formatter:on

    }

    private BigDecimal amountOwed(BillingAccount billingAccount, Collection<ARCode> acceptableArCodes, LogicalDate asOf) {
        List<InvoiceDebit> debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(billingAccount);
        List<InvoiceDebit> filteredDebits = N4GenerationUtils.filterDebits(debits, acceptableArCodes, asOf);
        InvoiceDebitAggregator debitAggregator = new InvoiceDebitAggregator();
        List<N4RentOwingForPeriod> rentOwingBreakdown = debitAggregator.debitsForPeriod(debitAggregator.aggregate(filteredDebits));

        BigDecimal amountOwed = BigDecimal.ZERO;
        for (N4RentOwingForPeriod rentOwingForPeriod : rentOwingBreakdown) {
            amountOwed = amountOwed.add(rentOwingForPeriod.rentOwing().getValue());
        }
        return amountOwed;
    }

    private boolean hasLegalStatus(Lease lease) {
        LegalStatus status = ServerSideFactory.create(LeaseLegalFacade.class).getCurrentLegalStatus(lease.<Lease> createIdentityStub());
        return status.status().getValue() != LegalStatus.Status.None;
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

    private N4Batch makeBatchData(N4BatchRequestDTO batchRequest) {
        N4Batch batchData = EntityFactory.create(N4Batch.class);
        // TODO batchData.name().set(batchRequest.batchName());

        batchData.noticeDate().setValue(batchRequest.noticeDate().getValue());
        batchData.deliveryMethod().setValue(batchRequest.deliveryMethod().getValue());
        batchData.companyLegalName().setValue(batchRequest.companyName().getValue());
        batchData.signingEmployee().set(Persistence.service().retrieve(Employee.class, batchRequest.agent().getPrimaryKey()));
        batchData.companyAddress().set(batchRequest.mailingAddress());

        batchData.companyPhoneNumber().setValue(batchRequest.phoneNumber().getValue());
        batchData.companyFaxNumber().setValue(batchRequest.faxNumber().getValue());
        batchData.companyEmailAddress().setValue(batchRequest.emailAddress().getValue());

        batchData.isLandlord().setValue(false); // TODO right now we always assume it's agent
        batchData.signatureDate().setValue(SystemDateManager.getLogicalDate());
        batchData.signature().setValue(retrieveSignature(batchData.signingEmployee()));
        return batchData;
    }

    private AptUnit unit(Lease leaseId) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        return lease.unit();
    }
}
