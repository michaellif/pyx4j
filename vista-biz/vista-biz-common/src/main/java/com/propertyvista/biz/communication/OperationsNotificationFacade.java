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
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import com.pyx4j.commons.UserRuntimeException;

import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;
import com.propertyvista.operations.domain.security.OperationsUser;

/**
 * @see also OperationsAlertFacade
 */
public interface OperationsNotificationFacade {

    void sendOperationsPasswordRetrievalToken(OperationsUser user) throws UserRuntimeException;

    void invalidDirectDebitReceived(DirectDebitRecord paymentRecord);

    void sendTenantSureCfcOperationProblem(Throwable error);

}
