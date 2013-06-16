/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.server.mail.SMTPMailServiceConfig;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.server.domain.security.CrmUserCredential;

class PreauthorizedPaymentAgreementMananger {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PreauthorizedPaymentAgreementMananger.class);

    PreauthorizedPayment persistPreauthorizedPayment(PreauthorizedPayment preauthorizedPayment, Tenant tenantId) {
        preauthorizedPayment.tenant().set(tenantId);
        Persistence.ensureRetrieve(preauthorizedPayment.tenant(), AttachLevel.Attached);

        LogicalDate nextPaymentDate = ServerSideFactory.create(PaymentMethodFacade.class).getNextScheduledPreauthorizedPaymentDate(
                preauthorizedPayment.tenant().lease());

        // Creates a new version of PAP if values changed and there are payments created
        if (!preauthorizedPayment.id().isNull()) {
            PreauthorizedPayment origPreauthorizedPayment = Persistence.service().retrieve(PreauthorizedPayment.class, preauthorizedPayment.getPrimaryKey());

            if (!EntityGraph.fullyEqual(origPreauthorizedPayment, preauthorizedPayment)) {
                // If tenant modifies PAP after cut off date - original will be used in this cycle and a new one in next cycle.
                LogicalDate cutOffDate = ServerSideFactory.create(PaymentMethodFacade.class).getPreauthorizedPaymentCutOffDate(
                        preauthorizedPayment.tenant().lease());

                boolean cutOffAppy = !origPreauthorizedPayment.effectiveFrom().isNull()
                        && origPreauthorizedPayment.effectiveFrom().getValue().before(nextPaymentDate);

                if (cutOffAppy && SystemDateManager.getDate().after(cutOffDate)) {
                    origPreauthorizedPayment.expiring().setValue(cutOffDate);
                    Persistence.service().merge(origPreauthorizedPayment);

                    preauthorizedPayment = EntityGraph.businessDuplicate(preauthorizedPayment);
                } else {
                    boolean hasPaymentRecords = false;
                    {
                        EntityQueryCriteria<PaymentRecord> criteria = new EntityQueryCriteria<PaymentRecord>(PaymentRecord.class);
                        criteria.eq(criteria.proto().preauthorizedPayment(), preauthorizedPayment);
                        hasPaymentRecords = Persistence.service().count(criteria) > 0;
                    }
                    if (hasPaymentRecords) {
                        origPreauthorizedPayment.isDeleted().setValue(Boolean.TRUE);
                        Persistence.service().merge(origPreauthorizedPayment);

                        preauthorizedPayment = EntityGraph.businessDuplicate(preauthorizedPayment);
                    }
                }
                preauthorizedPayment.effectiveFrom().setValue(nextPaymentDate);

            }
        } else {
            preauthorizedPayment.effectiveFrom().setValue(nextPaymentDate);

        }

        Persistence.service().merge(preauthorizedPayment);
        return preauthorizedPayment;
    }

    //If Tenant removes PAP - payment will NOT be canceled.
    void deletePreauthorizedPayment(PreauthorizedPayment preauthorizedPaymentId) {
        PreauthorizedPayment preauthorizedPayment = Persistence.service().retrieve(PreauthorizedPayment.class, preauthorizedPaymentId.getPrimaryKey());
        preauthorizedPayment.isDeleted().setValue(Boolean.TRUE);
        Persistence.service().merge(preauthorizedPayment);
    }

    List<PreauthorizedPayment> retrievePreauthorizedPayments(Tenant tenantId) {
        EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
        criteria.eq(criteria.proto().tenant(), tenantId);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        return Persistence.service().query(criteria);
    }

    void deletePreauthorizedPayments(LeasePaymentMethod paymentMethod) {
        EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
        criteria.eq(criteria.proto().paymentMethod(), paymentMethod);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);

        for (PreauthorizedPayment preauthorizedPayment : Persistence.service().query(criteria, AttachLevel.IdOnly)) {
            deletePreauthorizedPayment(preauthorizedPayment);
            new ScheduledPaymentsManager().cancelScheduledPayments(preauthorizedPayment);
        }

    }

    void suspendPreauthorizedPayment(PreauthorizedPayment preauthorizedPaymentId) {
        PreauthorizedPayment preauthorizedPayment = Persistence.service().retrieve(PreauthorizedPayment.class, preauthorizedPaymentId.getPrimaryKey());
        Persistence.service().retrieve(preauthorizedPayment.tenant());
        LogicalDate cutOffDate = ServerSideFactory.create(PaymentMethodFacade.class).getPreauthorizedPaymentCutOffDate(preauthorizedPayment.tenant().lease());
        DateUtils.addDays(cutOffDate, -1);
        preauthorizedPayment.expiring().setValue(cutOffDate);
        Persistence.service().merge(preauthorizedPayment);

        Lease lease = Persistence.service().retrieve(Lease.class, preauthorizedPayment.tenant().lease().getPrimaryKey());

        try {
            List<String> targetEmails = getEmailsForPapSuspensionNotification(lease);
            if (!targetEmails.isEmpty()) {
                ServerSideFactory.create(CommunicationFacade.class).sendPapSuspensionNotification(targetEmails, lease);
            } else {
                log.warn(
                        "Found no email addresses for PAP of {} suspension notifications (Add building property contact with name 'PAP_SUSPENTION_NOTIFICATIONS'",
                        lease.leaseId().getStringView());
            }
        } catch (Throwable e) {
            log.error("failed to send email", e);
        }
    }

    private List<String> getEmailsForPapSuspensionNotification(Lease lease) {
        List<String> emails = new ArrayList<String>();
        Persistence.service().retrieve(lease.unit().building());
        Persistence.service().retrieve(lease.unit().building().contacts().propertyContacts());

        for (PropertyContact contact : lease.unit().building().contacts().propertyContacts()) {
            if ("PAP_SUSPENSION_NOTIFICATIONS".equals(contact.name().getValue()) | "PAP_SUSPENTION_NOTIFICATIONS".equals(contact.name().getValue())) {
                emails.add(contact.email().getValue());
            }
        }

        if (!emails.isEmpty()) {
            SMTPMailServiceConfig mailConfig = (SMTPMailServiceConfig) ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
            if (CommonsStringUtils.isStringSet(mailConfig.getForwardAllTo())) {
                String forwardToEmail = mailConfig.getForwardAllTo();
                int s = emails.size();
                emails.clear();
                for (int i = 0; i < s; ++i) {
                    emails.add(forwardToEmail);
                }
            }
            return emails;
        } else {
            return getPmcAccountOwnerEmails();
        }

    }

    private List<String> getPmcAccountOwnerEmails() {
        List<String> accountOwnerEmails = new ArrayList<String>();

        EntityQueryCriteria<CrmUserCredential> criteria = EntityQueryCriteria.create(CrmUserCredential.class);
        criteria.eq(criteria.proto().roles().$().behaviors(), VistaCrmBehavior.PropertyVistaAccountOwner);
        List<CrmUserCredential> accountOwnerCredentials = Persistence.service().query(criteria);
        for (CrmUserCredential accountOwnerCredential : accountOwnerCredentials) {
            Persistence.service().retrieve(accountOwnerCredential.user());
            if (!accountOwnerCredential.user().email().isNull()) {
                accountOwnerEmails.add(accountOwnerCredential.user().email().getValue());
            }
        }

        return accountOwnerEmails;
    }
}
