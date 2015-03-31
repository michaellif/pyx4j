/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 30, 2015
 * @author ernestog
 */
package com.propertyvista.preloader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.legal.eviction.EvictionCaseFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.eviction.EvictionStatusN4;
import com.propertyvista.domain.eviction.EvictionStatusRecord;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4BatchItem;
import com.propertyvista.domain.legal.n4.N4Data.TerminationDateOption;
import com.propertyvista.domain.legal.n4.N4DeliveryMethod;
import com.propertyvista.domain.legal.n4.N4LeaseArrears;
import com.propertyvista.domain.legal.n4.N4UnpaidCharge;
import com.propertyvista.domain.policy.policies.EvictionFlowPolicy;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.policy.policies.N4Policy.EmployeeSelectionMethod;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.generator.util.RandomUtil;

public class N4BatchesPreloader extends BaseVistaDevDataPreloader {

    private int nBatches = 0;

    @Override
    public String create() {

        if (!ApplicationMode.isDemo()) {
            return null;
        }

        N4Policy policy = Persistence.service().retrieve(EntityQueryCriteria.create(N4Policy.class));

        List<Lease> candidateLeases = getSomeLeasesOwningMoney();

        for (Lease lease : candidateLeases) {
            Persistence.ensureRetrieve(lease.currentTerm().unit().building(), AttachLevel.Attached);
            Building building = lease.currentTerm().unit().building();

            N4Batch batch = createBatch(building, policy);
            Persistence.service().persist(batch);

            N4BatchItem item = createBatchItem(batch, lease);
            Persistence.service().persist(item);

            EvictionCase evictionCase = createEvictionCase(lease);
            Persistence.service().persist(evictionCase);

            EvictionStatusN4 n4status = createEvictionStatusN4(evictionCase);
            evictionCase.history().add(n4status);

            EvictionStatusRecord record = createEvictionStatusRecord();
            n4status.statusRecords().add(record);
            Persistence.service().persist(n4status);

            n4status.leaseArrears().set(item.leaseArrears());
            n4status.originatingBatch().set(item.batch());
            n4status.cancellationBalance().setValue(policy.cancellationThreshold().getValue());
            Persistence.service().persist(n4status);

        }

        return "Created " + nBatches + " N4Batches";
    }

    private EvictionStatusRecord createEvictionStatusRecord() {
        EvictionStatusRecord record = EntityFactory.create(EvictionStatusRecord.class);
        record.addedBy().set(getRandomEmployee());
        record.note().setValue("Eviction Status Record saved");
        return record;
    }

    private EvictionStatusN4 createEvictionStatusN4(EvictionCase evictionCase) {
        EvictionFlowStep flowStep = null;
        for (EvictionFlowStep step : evictionCase.evictionFlowPolicy().evictionFlow()) {
            if (step.name().getValue().equals("N4")) {
                flowStep = step;
                break;
            }
        }

        EvictionStatusN4 n4status = EntityFactory.create(EvictionStatusN4.class);
        n4status.evictionStep().set(flowStep);
        n4status.addedBy().set(getRandomEmployee());
        n4status.note().setValue("Auto-generated for Eviction Status update");
        n4status.terminationDate().setValue(new LogicalDate(TimeUtils.today()));

        return n4status;
    }

    private EvictionCase createEvictionCase(Lease lease) {
        EvictionCase evictionCase = EntityFactory.create(EvictionCase.class);
        evictionCase.lease().set(lease);
        evictionCase.note().setValue("Created by N4 Batch process");
        evictionCase.evictionFlowPolicy().set(getEvictionFlowPolicy(lease));
        evictionCase.createdBy().set(getRandomEmployee());
        return evictionCase;
    }

    private N4BatchItem createBatchItem(N4Batch batch, Lease lease) {
        N4BatchItem item = EntityFactory.create(N4BatchItem.class);
        item.leaseArrears().set(getLeaseArrears(lease));
        item.lease().set(lease);
        item.batch().set(batch);
        batch.items().add(item);
        Persistence.service().persist(item.leaseArrears());
        return item;
    }

    private List<Lease> getSomeLeasesOwningMoney() {
        List<Lease> candidateLeases = new ArrayList<Lease>();

        // Retrieve lease records
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);

        // Potential Owning Leases from different buildings
        for (Lease lease : Persistence.service().query(criteria, AttachLevel.IdOnly)) {
            Persistence.ensureRetrieve(lease.currentTerm().unit().building(), AttachLevel.Attached);
            if (!hasOpenCase(lease) && hasAmountOwed(lease, new BigDecimal(0))) {
                if (candidateLeases.isEmpty()) {
                    candidateLeases.add(lease);
                } else if (isDifferentBuilding(candidateLeases.get(nBatches), lease)) {
                    candidateLeases.add(lease);
                    if (++nBatches >= 4) {
                        break;
                    }
                }
            }
        }

        return candidateLeases;
    }

    private String getGeneratedBatchName(N4Batch batch, Building building) {
        return building.propertyCode().getValue() + "_" + batch.created().getStringView().replaceAll(" ", "_");
    }

    private boolean isDifferentBuilding(Lease lease1, Lease lease2) {
        Key building1Pk = lease1.currentTerm().unit().building().getPrimaryKey();
        Key building2Pk = lease2.currentTerm().unit().building().getPrimaryKey();
        return !(building1Pk.equals(building2Pk));
    }

    private Employee getRandomEmployee() {
        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
        criteria.add(PropertyCriterion.ne(criteria.proto().email(), "support@propertyvista.com"));
        List<Employee> employees = Persistence.service().query(criteria);

        // Different employees for different steps of this N4Case
        int index = RandomUtil.nextInt(employees.size(), "employees", 4);
        return employees.get(index);
    }

    private EvictionFlowPolicy getEvictionFlowPolicy(Lease lease) {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);

        Building building = lease.unit().building();
        EvictionFlowPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, EvictionFlowPolicy.class);

        if (policy == null) {
            throw new Error("Cannot find EvictionFlowPolicy for building: " + building.propertyCode().getValue());
        }
        return policy;
    }

    private N4Batch createBatch(Building building, N4Policy n4policy) {
        N4Batch batch = EntityFactory.create(N4Batch.class);

        batch.building().set(building);
        batch.created().setValue(SystemDateManager.getDate());
        batch.companyLegalName().setValue(n4policy.companyName().getValue());
        batch.companyAddress().set(n4policy.mailingAddress().duplicate(InternationalAddress.class));

        Employee employee = getRandomEmployee();
        // signing Agent
        if (EmployeeSelectionMethod.ByLoggedInUser.equals(n4policy.agentSelectionMethodN4().getValue())) {
            batch.signingAgent().set(employee);
        }

        batch.useAgentContactInfoN4().set(n4policy.useAgentContactInfoN4());
        if (batch.useAgentContactInfoN4().getValue(false) && !batch.signingAgent().isNull()) {
            batch.phoneNumber().set(batch.signingAgent().workPhone());
        } else {
            batch.phoneNumber().setValue(n4policy.phoneNumber().getValue());
            batch.faxNumber().setValue(n4policy.faxNumber().getValue());
            batch.emailAddress().setValue(n4policy.emailAddress().getValue());
        }

        // servicing Agent
        if (EmployeeSelectionMethod.ByLoggedInUser.equals(n4policy.agentSelectionMethodCS().getValue())) {
            batch.servicingAgent().set(employee);
        }

        batch.useAgentContactInfoCS().set(n4policy.useAgentContactInfoCS());
        if (batch.useAgentContactInfoCS().getValue(false) && !batch.servicingAgent().isNull()) {
            batch.phoneNumberCS().set(batch.servicingAgent().workPhone());
        } else {
            batch.phoneNumberCS().setValue(n4policy.phoneNumberCS().getValue());
        }

        // Add some additional data for Form Batch Presentation
        batch.name().setValue(getGeneratedBatchName(batch, building));
        batch.deliveryMethod().setValue(RandomUtil.random(N4DeliveryMethod.values()));
        batch.isReadyForService().setValue(Boolean.TRUE);
        batch.terminationDateOption().setValue(TerminationDateOption.Calculate);

        // keep the policy reference
        batch.n4policy().set(n4policy);

        return batch;
    }

    private boolean hasOpenCase(Lease lease) {
        return ServerSideFactory.create(EvictionCaseFacade.class).getCurrentEvictionCase(lease) != null;
    }

    private boolean hasAmountOwed(Lease lease, BigDecimal minAmountOwed) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);

        BigDecimal amountOwed = BigDecimal.ZERO;
        for (N4UnpaidCharge rentOwingForPeriod : getUnpaidCharges(lease)) {
            amountOwed = amountOwed.add(rentOwingForPeriod.rentOwing().getValue());
        }

        return amountOwed.compareTo(minAmountOwed) > 0;
    }

    public static List<N4UnpaidCharge> getUnpaidCharges(Lease lease) {
        LogicalDate today = SystemDateManager.getLogicalDate();

        List<InvoiceDebit> debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(lease.billingAccount());
        List<InvoiceDebit> filteredDebits = filterDebits(debits, getPolicy(lease).relevantARCodes(), today);
        List<N4UnpaidCharge> owings = new ArrayList<>();
        for (InvoiceDebit debit : filteredDebits) {
            N4UnpaidCharge owing = EntityFactory.create(N4UnpaidCharge.class);
            owing.fromDate().setValue(debit.billingCycle().billingCycleStartDate().getValue());
            owing.toDate().setValue(debit.billingCycle().billingCycleEndDate().getValue());
            owing.rentCharged().setValue(owing.rentCharged().getValue(BigDecimal.ZERO).add(debit.amount().getValue()));
            owing.rentCharged().setValue(owing.rentCharged().getValue().add(debit.taxTotal().getValue()));
            owing.rentOwing().setValue(owing.rentOwing().getValue(BigDecimal.ZERO).add(debit.outstandingDebit().getValue()));
            owing.rentPaid().setValue(owing.rentCharged().getValue().subtract(owing.rentOwing().getValue()));
            owing.arCode().set(debit.arCode());
            owings.add(owing);
        }
        return owings;
    }

    private static N4Policy getPolicy(Lease lease) {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.IdOnly);
        return ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(), N4Policy.class);
    }

    private static List<InvoiceDebit> filterDebits(List<InvoiceDebit> debits, Collection<ARCode> acceptableArCodes, LogicalDate asOf) {
        List<InvoiceDebit> filteredDebits = new ArrayList<InvoiceDebit>(debits.size());
        for (InvoiceDebit debit : debits) {
            if (acceptableArCodes.contains(debit.arCode()) && debit.dueDate().getValue().compareTo(asOf) < 0) {
                filteredDebits.add(debit);
            }
        }
        return filteredDebits;
    }

    private N4LeaseArrears getLeaseArrears(Lease lease) {
        BigDecimal amountOwed = BigDecimal.ZERO;
        List<N4UnpaidCharge> owings;
        for (N4UnpaidCharge rentOwingForPeriod : owings = getUnpaidCharges(lease)) {
            amountOwed = amountOwed.add(rentOwingForPeriod.rentOwing().getValue());
        }

        N4LeaseArrears leaseArrears = EntityFactory.create(N4LeaseArrears.class);
        leaseArrears.lease().set(lease);
        leaseArrears.unpaidCharges().addAll(owings);
        leaseArrears.totalRentOwning().setValue(amountOwed);

        return leaseArrears;
    }

    @Override
    public String delete() {
        // TODO Auto-generated method stub
        return null;
    }

}
