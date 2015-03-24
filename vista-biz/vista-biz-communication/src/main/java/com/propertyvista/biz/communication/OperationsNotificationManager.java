/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 2, 2014
 * @author stanp
 */
package com.propertyvista.biz.communication;

import java.util.concurrent.Callable;

import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.server.mail.MessageTemplate;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.tenant.insurance.errors.CfcApiException;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.server.TaskRunner;

public class OperationsNotificationManager {

    private final static I18n i18n = I18n.get(OperationsNotificationManager.class);

    private static String wrapperTextResourceName = "email/notification/template-body-operations.html";

    private static String getSender() {
        return ServerSideConfiguration.instance().getApplicationEmailSender();
    }

    public static MailMessage createOperationsPasswordResetEmail(AbstractUser user, String token) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());
        email.setTo(AddresseUtils.getCompleteEmail(user));
        email.setSubject(i18n.tr("Vista Operations Password Retrieval"));

        MessageTemplate template = new MessageTemplate();
        template.setBodyTemplate("Dear ${name},<br/>\n"
                + "This email was sent to you in response to your request to modify your Property Vista Support Administration account password.<br/>\n"
                + "Click the link below to go to the Property Vista Operations site and create new password for your account:<br/>\n"
                + "    <a href=\"${link}\">Change Your Password</a>");

        template.variable("${name}", user.name());
        template.variable("${link}", AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.operations, true), true,
                OperationsSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, token));

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createInvalidDirectDebitReceivedEmail(DirectDebitRecord paymentRecord) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());
        email.addToList("leonard@propertyvista.com, support@propertyvista.com");
        email.setSubject(i18n.tr("Invalid Direct Debit Received"));

        MessageTemplate template = new MessageTemplate();
        template.setBodyTemplate("Invalid Direct Debit received. See <a href=\"${link}\">Payment Record details</a><br/>${operationsNotes}");

        template.variable("${link}", AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.operations, true), true,
                new OperationsSiteMap.FundsTransfer.DirectDebitRecord().formViewerPlace(paymentRecord.getPrimaryKey())));
        template.variable("${operationsNotes}", paymentRecord.operationsNotes());

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createNewMerchantAccountRequested(final MerchantAccount merchantAccount) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();

        PmcMerchantAccountIndex macc = TaskRunner.runInOperationsNamespace(new Callable<PmcMerchantAccountIndex>() {
            @Override
            public PmcMerchantAccountIndex call() {
                EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
                criteria.eq(criteria.proto().pmc(), pmc);
                criteria.eq(criteria.proto().merchantAccountKey(), merchantAccount.getPrimaryKey());
                return Persistence.service().retrieve(criteria);
            }
        });

        MailMessage email = new MailMessage();
        email.setSender(getSender());
        email.addToList("leonard@propertyvista.com, support@propertyvista.com");
        email.setSubject(i18n.tr("New Merchant Account requested by {0}", pmc.name()));

        MessageTemplate template = new MessageTemplate();
        template.setBodyTemplate("New Merchant Account requested. See <a href=\"${link}\">Account details</a>");

        template.variable("${link}", AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.operations, true), true,
                new OperationsSiteMap.Management.PmcMerchantAccount().formViewerPlace(macc.getPrimaryKey())));

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createBuildingSuspendedEmail(Building building) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        String newStatus = building.suspended().getValue(false) ? "Suspended" : "Unsuspended";
        MailMessage email = new MailMessage();
        email.setSender(getSender());
        email.addToList("leonard@propertyvista.com, support@propertyvista.com");
        email.setSubject(i18n.tr("Building {0} is {1} in PMC {2}", building.propertyCode(), newStatus, pmc.name()));

        MessageTemplate template = new MessageTemplate();
        template.setBodyTemplate("Building ${propertyCode} has been ${status}.<br/>\nPlease confirm with customer ${pmc}!");

        template.variable("${propertyCode}", building.propertyCode());
        template.variable("${status}", newStatus);
        template.variable("${pmc}", pmc.name());

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createCfcErrorMessage(Throwable error) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());
        email.setTo("support@propertyvista.com");
        if (error instanceof CfcApiException) {
            email.setSubject("TenantSure CFC API error");
        } else if (error instanceof WebServiceException) {
            email.setSubject("TenantSure CFC API web service error");
        } else {
            email.setSubject("TenantSure error");
        }
        email.setHtmlBody(SystemDateManager.getDate() + "<br>" + error.getMessage() + "<br><pre>" + ExceptionUtils.getStackTrace(error) + "</pre>");

        return email;
    }

}
