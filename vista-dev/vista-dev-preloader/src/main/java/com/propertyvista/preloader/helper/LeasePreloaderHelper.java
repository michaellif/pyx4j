/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.preloader.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodTarget;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementConfirmationTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementLegalTerm;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.AgreementDigitalSignatures;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.SignedAgreementConfirmationTerm;
import com.propertyvista.domain.tenant.lease.SignedAgreementLegalTerm;
import com.propertyvista.generator.LeaseGenerator;

/**
 * Some helper methods used during Lease preload
 *
 * @author ernestog
 *
 */
public class LeasePreloaderHelper {

    private static Logger log = LoggerFactory.getLogger(LeasePreloaderHelper.class);

    public static void createDefaultAutoPayment(LeaseParticipant<?> leaseParticipant, BillableItem item) {
        if (item == null) {
            log.info("No billable item set for tenant '{}'. Could not set default AutoPayment during preload.", leaseParticipant.customer().person().email()
                    .getValue());
            return;
        }

        // Retrieve payment methods for tenant
        List<LeasePaymentMethod> methods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(leaseParticipant,
                PaymentMethodTarget.AutoPaySetup, VistaApplication.crm);
        if (methods.isEmpty()) {
            log.info("No PaymentMethods set for tenant '{}'. Could not set default AutoPayment during preload.", leaseParticipant.customer().person().email()
                    .getValue());
            return;
        }

        AutopayAgreement autoPayAgreement = EntityFactory.create(AutopayAgreement.class);
        Persistence.ensureRetrieve(leaseParticipant, AttachLevel.Attached);
        autoPayAgreement.tenant().set(leaseParticipant);
        autoPayAgreement.effectiveFrom().setValue(ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(autoPayAgreement.tenant().lease()));
        autoPayAgreement.paymentMethod().set(methods.get(0));

        // Setup autopay with the amount of money for the item
        AutopayAgreementCoveredItem coveredItems = EntityFactory.create(AutopayAgreementCoveredItem.class);
        coveredItems.amount().set(item.agreedPrice());
        coveredItems.billableItem().set(item);
        coveredItems.pap().set(autoPayAgreement);
        autoPayAgreement.coveredItems().add(coveredItems);

        ServerSideFactory.create(PaymentMethodFacade.class).persistAutopayAgreement(autoPayAgreement, autoPayAgreement.tenant());
    }

    public static void signDefaultAgreement(final LeaseParticipant<?> leaseParticipant) {

        List<SignedAgreementLegalTerm> legalTerms = new ArrayList<SignedAgreementLegalTerm>();
        List<SignedAgreementConfirmationTerm> confirmationTerms = new ArrayList<SignedAgreementConfirmationTerm>();
        AgreementDigitalSignatures agreementSignatures = EntityFactory.create(AgreementDigitalSignatures.class);

        Lease lease = leaseParticipant.lease();

        Persistence.ensureRetrieve(lease.currentTerm().agreementLegalTerms(), AttachLevel.Attached);
        for (LeaseAgreementLegalTerm term : lease.currentTerm().agreementLegalTerms()) {
            SignedAgreementLegalTerm signedTerm = EntityFactory.create(SignedAgreementLegalTerm.class);
            signedTerm.term().set(term);
            signedTerm.signature().signatureFormat().set(term.signatureFormat());
            legalTerms.add(signedTerm);
        }

        Persistence.ensureRetrieve(lease.currentTerm().agreementConfirmationTerms(), AttachLevel.Attached);
        for (LeaseAgreementConfirmationTerm term : lease.currentTerm().agreementConfirmationTerms()) {
            SignedAgreementConfirmationTerm signedTerm = EntityFactory.create(SignedAgreementConfirmationTerm.class);
            signedTerm.term().set(term);
            signedTerm.signature().signatureFormat().set(term.signatureFormat());
            confirmationTerms.add(signedTerm);
        }

        // Sign
        agreementSignatures.leaseParticipant().set(leaseParticipant);
        agreementSignatures.legalTermsSignatures().addAll(legalTerms);
        agreementSignatures.confirmationTermSignatures().addAll(confirmationTerms);

        Persistence.secureSave(agreementSignatures);

    }

    public static void addDefaultPaymentToLeaseApplication(LeaseTermParticipant<? extends LeaseParticipant<?>> leaseTermParticipant) {
        // Retrieve payment methods
        List<LeasePaymentMethod> methods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(leaseTermParticipant,
                PaymentMethodTarget.OneTimePayment, VistaApplication.prospect);

        Persistence.ensureRetrieve(leaseTermParticipant.leaseParticipant().customer().personScreening().screene().paymentMethods(), AttachLevel.Attached);

        if (methods.isEmpty()) {
            methods = new ArrayList<LeasePaymentMethod>();
            methods.add(LeaseGenerator.createPaymentMethod((LeaseTermTenant) leaseTermParticipant));
        }

        // Create payment
        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.amount().setValue(new BigDecimal(80 + Math.abs(new Random().nextInt() % 40)));
        paymentRecord.billingAccount().set(leaseTermParticipant.leaseParticipant().lease().billingAccount());
        paymentRecord.leaseTermParticipant().set(leaseTermParticipant);
        paymentRecord.paymentMethod().set(methods.get(0));

        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
    }

}
