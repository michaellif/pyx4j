/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;
import com.propertyvista.operations.domain.security.OperationsUser;
import com.propertyvista.operations.domain.security.OperationsUserCredential;
import com.propertyvista.server.common.security.AccessKey;

public class OperationsNotificationFacadeImpl implements OperationsNotificationFacade {

    private static final I18n i18n = I18n.get(OperationsNotificationFacadeImpl.class);

    private static final Logger log = LoggerFactory.getLogger(OperationsNotificationFacadeImpl.class);

    final static String GENERIC_FAILED_MESSAGE = "Invalid User Account";

    final static String GENERIC_UNAVAIL_MESSAGE = "Mail Service Is Temporary Unavailable. Please Try Again Later.";

    @Override
    public void sendOperationsPasswordRetrievalToken(OperationsUser user) {
        String token = AccessKey.createAccessToken(user, OperationsUserCredential.class, 1);
        if (token != null) {
            send(OperationsNotificationManager.createOperationsPasswordResetEmail(user, token));
        } else {
            log.error(GENERIC_FAILED_MESSAGE);
        }
    }

    @Override
    public void invalidDirectDebitReceived(DirectDebitRecord paymentRecord) {
        send(OperationsNotificationManager.createInvalidDirectDebitReceivedEmail(paymentRecord));
    }

    @Override
    public void sendTenantSureCfcOperationProblem(Throwable error) {
        send(OperationsNotificationManager.createCfcErrorMessage(error));
    }

    private void send(MailMessage m) {
        try {
            if (MailDeliveryStatus.Success != Mail.send(m)) {
                throw new UserRuntimeException(i18n.tr(GENERIC_UNAVAIL_MESSAGE));
            }
        } catch (Throwable t) {
            log.error("Could not send message", t);
        }
    }
}
