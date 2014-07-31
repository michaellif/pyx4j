/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 31, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi.stubs;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ExceptionHandler;

import com.propertyvista.biz.system.YardiPropertyNoAccessException;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.yardi.services.YardiHandledErrorMessages;

public class ResidentTransactionsExceptionHandler implements ExceptionHandler {

    @Override
    public void handle(Throwable exception) throws Throwable {
        if (exception instanceof YardiServiceMessageException) {
            String propertyCode = "";
            YardiServiceMessageException e = (YardiServiceMessageException) exception;
            if (e.getMessages().hasErrorMessage(YardiHandledErrorMessages.errorMessage_NoAccess)) {
                throw new YardiPropertyNoAccessException(e.getMessages().getErrorMessage().getValue());
            } else if (e.getMessages().hasErrorMessage(YardiHandledErrorMessages.errorMessage_TenantNotFound)) {
                // All Ok there are no transactions
            } else {
                YardiLicense.handleVendorLicenseError(e.getMessages());
                throw new YardiServiceException(SimpleMessageFormat.format("{0}; PropertyId {1}", e.getMessages().toString(), propertyCode));
            }
        } else {
            throw exception;
        }
    }

}
