/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 29, 2013
 * @author vlads
 */
package com.propertyvista.biz.financial.payment;

import java.util.concurrent.Callable;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.communication.OperationsNotificationFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.biz.system.eft.EFTTransportFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.MerchantAccount.MerchantElectronicPaymentSetup;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitFile;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecordProcessingStatus;
import com.propertyvista.server.TaskRunner;

class DirectDebitReceiveProcessor {

    Integer receiveBmoFiles(final ExecutionMonitor executionMonitor) {
        final DirectDebitFile directDebitFile;
        try {
            directDebitFile = ServerSideFactory.create(EFTTransportFacade.class).receiveBmoFile();
        } catch (SftpTransportConnectionException e) {
            executionMonitor.addInfoEvent("Pooled, Can't connect to server", e.getMessage());
            return null;
        }
        if (directDebitFile == null) {
            executionMonitor.addInfoEvent("Pooled, No file found on server", null);
            return null;
        } else {
            executionMonitor.addInfoEvent("received file", directDebitFile.fileName().getValue());
            executionMonitor.addInfoEvent("fileSerialDate", directDebitFile.fileSerialDate().getStringView());
            executionMonitor.addInfoEvent("fileSerialNumber", directDebitFile.fileSerialNumber().getValue());
        }

        boolean processedOk = false;
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    validateAndPersistFile(executionMonitor, directDebitFile);
                    return null;
                }
            });
            processedOk = true;
        } finally {
            ServerSideFactory.create(EFTTransportFacade.class).confirmReceivedBmoFile(directDebitFile.fileName().getValue(), !processedOk);
        }
        return directDebitFile.records().size();
    }

    private void validateAndPersistFile(ExecutionMonitor executionMonitor, DirectDebitFile directDebitFile) {
        {
            EntityQueryCriteria<DirectDebitFile> criteria = EntityQueryCriteria.create(DirectDebitFile.class);
            criteria.eq(criteria.proto().fileSerialNumber(), directDebitFile.fileSerialNumber());
            criteria.eq(criteria.proto().fileSerialDate(), directDebitFile.fileSerialDate());
            if (Persistence.service().count(criteria) > 0) {
                throw new Error("Duplicate DirectDebit file received " + directDebitFile.fileSerialNumber().getValue() + " "
                        + directDebitFile.fileSerialDate().getValue());
            }
        }

        Persistence.service().persist(directDebitFile);
        for (final DirectDebitRecord record : directDebitFile.records()) {

            // Verify and find PMC and account
            if (ValidationUtils.isVistaAccountNumberValid(record.accountNumber().getValue())) {
                record.pmc().set(ServerSideFactory.create(IdAssignmentFacade.class).getPmcByAccountNumber(record.accountNumber().getValue()));
                if (record.pmc().isNull()) {
                    record.processingStatus().setValue(DirectDebitRecordProcessingStatus.Invalid);
                    addOperationsNotes(record, "Account Number Range Not assigned to PMC");
                } else {
                    // verify we actually have the account

                    BillingAccount billingAccount = TaskRunner.runInTargetNamespace(record.pmc(), new Callable<BillingAccount>() {
                        @Override
                        public BillingAccount call() {
                            BillingAccount billingAccount;
                            {
                                EntityQueryCriteria<BillingAccount> criteria = EntityQueryCriteria.create(BillingAccount.class);
                                criteria.eq(criteria.proto().accountNumber(), record.accountNumber());
                                billingAccount = Persistence.service().retrieve(criteria);
                            }
                            if (billingAccount == null) {
                                addOperationsNotes(record, "Billing Account Not found in PMC '" + record.pmc().name().getStringView() + "'");
                            } else {
                                // Sold building validation
                                EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
                                criteria.eq(criteria.proto().units().$().leases(), billingAccount.lease());
                                Building building = Persistence.service().retrieve(criteria);
                                if (building.suspended().getValue(false)) {
                                    notifyTenant(record, getLeaseTermParticipant(record, billingAccount));
                                    billingAccount = null;
                                    addOperationsNotes(record, "Building " + building.propertyCode().getStringView() + " Suspended in PMC '"
                                            + record.pmc().name().getStringView() + "'");
                                } else {
                                    // Validate MID
                                    MerchantElectronicPaymentSetup setup = PaymentUtils.getEffectiveElectronicPaymentsSetup(building);
                                    if (!setup.acceptedDirectBanking().getValue(false)) {
                                        addOperationsNotes(record, "No MID or Building " + building.propertyCode().getStringView()
                                                + " is not setup to accept DirectBanking in PMC '" + record.pmc().name().getStringView() + "'");
                                    }
                                }
                            }
                            return billingAccount;
                        }
                    });

                    if (billingAccount == null) {
                        record.processingStatus().setValue(DirectDebitRecordProcessingStatus.Invalid);
                    } else {
                        record.processingStatus().setValue(DirectDebitRecordProcessingStatus.Received);
                    }
                }
            } else {
                record.processingStatus().setValue(DirectDebitRecordProcessingStatus.Invalid);
                addOperationsNotes(record, "Account Number Invalid Format");
            }

            Persistence.service().persist(record);

            if (record.processingStatus().getValue() == DirectDebitRecordProcessingStatus.Received) {
                executionMonitor.addProcessedEvent("payment", record.amount().getValue());
            } else {
                executionMonitor.addFailedEvent("payment", record.amount().getValue());
                ServerSideFactory.create(OperationsNotificationFacade.class).invalidDirectDebitReceived(record);
            }
        }
    }

    private static void addOperationsNotes(DirectDebitRecord record, String message) {
        record.operationsNotes().setValue( //
                CommonsStringUtils.nvl_concat(record.operationsNotes().getValue(), message, "\n"));
    }

    private static void notifyTenant(DirectDebitRecord record, LeaseTermParticipant<?> leaseTermParticipant) {
        ServerSideFactory.create(NotificationFacade.class).directDebitToSoldBuilding(record, leaseTermParticipant);
    }

    private LeaseTermParticipant<?> getLeaseTermParticipant(DirectDebitRecord debitRecord, BillingAccount billingAccount) {
        Persistence.service().retrieve(billingAccount.lease());
        Persistence.service().retrieve(billingAccount.lease().currentTerm().version().tenants());
        Persistence.service().retrieve(billingAccount.lease().currentTerm().version().guarantors());

        // Find tenant by name in debitRecord.customerName()
        LeaseTermParticipant<? extends LeaseParticipant<?>> leaseTermParticipant = billingAccount.lease().currentTerm().version().tenants().get(0);
        String customerNametoMatch = DirectDebitPostProcessor.normalizeName(debitRecord.customerName().getStringView().replace(',', ' '));
        leaseTermParticipant = DirectDebitPostProcessor.findTenantByName(customerNametoMatch, billingAccount);

        return leaseTermParticipant;
    }
}
