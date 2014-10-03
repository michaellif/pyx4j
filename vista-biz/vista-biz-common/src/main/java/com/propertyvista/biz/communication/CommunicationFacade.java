/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;

/**
 * to Tenant/Applicant (Lease)
 * to Guarantor (Lease)
 * to Lead(Guest) (Building)
 */
public interface CommunicationFacade {

    void sendCrmPasswordRetrievalToken(CrmUser user) throws UserRuntimeException;

    /**
     * customer may be Guarantor as well
     */
    void sendProspectPasswordRetrievalToken(Customer customer) throws UserRuntimeException;

    void sendTenantPasswordRetrievalToken(Customer customer) throws UserRuntimeException;

    void sendProspectWelcome(LeaseTermTenant tenant);

    void sendApplicantApplicationInvitation(LeaseTermTenant tenant);

    void sendCoApplicantApplicationInvitation(LeaseTermTenant tenant);

    void sendGuarantorApplicationInvitation(LeaseTermGuarantor guarantor);

    void sendApplicationDocumentCopy(LeaseApplicationDocument documentId);

    void sendApplicationApproved(LeaseTermParticipant<?> participantId);

    void sendApplicationDeclined(LeaseTermParticipant<?> participantId);

    void sendTenantInvitation(LeaseTermTenant tenant);

    void sendDirectDebitAccountChangedNote(LeaseTermTenant tenant);

    void sendNewPmcEmail(OnboardingUser user, Pmc pmc);

    void sendTenantSurePaymentNotProcessedEmail(Tenant tenant, LogicalDate gracePeriodEndDate, LogicalDate cancellationDate);

    void sendTenantSurePaymentsResumedEmail(Tenant tenant);

    void sendTenantSureRenewalEmail(String tenantEmail, TenantSureInsurancePolicy policy);

    void sendTenantSureCCExpiringEmail(Person tenant, String ccLastDigits, LogicalDate ccExpiry);

    void sendPaymentRejectedNotification(List<String> targetEmail, PaymentRecord paymentRecord, boolean applyNSF);

    void sendPaymentYardiUnableToRejectNotification(List<String> targetEmail, PaymentRecord paymentRecord, boolean applyNSF, String yardiErrorMessage);

    void sendAutoPayReviewRequiredNotification(List<String> targetEmails, List<Lease> leaseIds);

    void sendAutoPayCancelledBySystemNotification(List<String> targetEmails, List<Lease> leaseIds, Map<Lease, List<AutopayAgreement>> canceledAgreements);

    void sendAutoPayCancelledByResidentNotification(List<String> targetEmails, Lease leaseId, List<AutopayAgreement> canceledAgreements);

    void sendAutoPayCreatedByResidentNotification(List<String> targetEmails, Lease leaseId, AutopayAgreement createdAgreement);

    void sendTenantOneTimePaymentSubmitted(PaymentRecord paymentRecord);

    void sendTenantPaymentRejected(PaymentRecord paymentRecord, boolean applyNSF);

    void sendTenantPaymentCleared(PaymentRecord paymentRecord);

    void sendTenantAutoPaySetupCompleted(AutopayAgreement autopayAgreement);

    void sendTenantAutoPayChanges(AutopayAgreement autopayAgreement);

    void sendTenantAutoPayCancellation(AutopayAgreement autopayAgreement);

    void sendOnlinePaymentSetupCompletedEmail(String userName, String userEmail);

    //void sendCustomerMessage(CustomerCustomMessageTemplate customMessageTemplate, Customer customer);

    //void sendEmployeeMessage(EmployeeMessageType employeeMessageType, Employee employee);

//    MailMessage sendMaintenanceRequestEmail(String sendTo, String userName, MaintenanceRequest request, boolean isNewRequest, boolean toAdmin);

    void sendMaintenanceRequestCreatedPMC(MaintenanceRequest request);

    void sendMaintenanceRequestCreatedTenant(MaintenanceRequest request);

    void sendMaintenanceRequestUpdated(MaintenanceRequest request);

    void sendMaintenanceRequestCompleted(MaintenanceRequest request);

    void sendMaintenanceRequestCancelled(MaintenanceRequest request);

    MailMessage sendMaintenanceRequestEntryNotice(MaintenanceRequest request);

    void sendYardiConfigurationNotification(List<String> sendTo, String message);
}
