/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 1, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.stubs;

import java.rmi.RemoteException;

import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.system.yardi.UnableToPostTerminalYardiServiceException;
import com.propertyvista.biz.system.yardi.YardiPropertyNoAccessException;
import com.propertyvista.biz.system.yardi.YardiResponseException;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.beans.Messages;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.services.YardiHandledErrorMessages;

/*
 * This class is used to provide an access for the application to a specific Yardi interface.
 * It's main functions are the following:
 * - create an instance of a proper yardi stub implementation delegate
 * - call corresponding delegate method and validate response for possible errors
 */
public class YardiResidentTransactionsStubProxy extends YardiAbstractStubProxy implements YardiResidentTransactionsStub {

    public YardiResidentTransactionsStubProxy() {
        setMessageErrorHandler(noPropertyAccessHandler);
    }

    private YardiResidentTransactionsStub getStub(PmcYardiCredential yc) {
        return getStubInstance(YardiResidentTransactionsStub.class, yc);
    }

    @Override
    public String ping(PmcYardiCredential yc) {
        return getStub(yc).ping(yc);
    }

    @Override
    public String getPluginVersion(PmcYardiCredential yc) {
        return getStub(yc).getPluginVersion(yc);
    }

    @Override
    public void validate(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        getStub(yc).validate(yc);
    }

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        try {
            return getStub(yc).getPropertyConfigurations(yc);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public ResidentTransactions getAllResidentTransactions(PmcYardiCredential yc, String propertyListCode) throws YardiServiceException, RemoteException {
        try {
            return getStub(yc).getAllResidentTransactions(yc, propertyListCode);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public ResidentTransactions getResidentTransactionsForTenant(PmcYardiCredential yc, String propertyId, String tenantId) throws YardiServiceException,
            RemoteException {
        try {
            return getStub(yc).getResidentTransactionsForTenant(yc, propertyId, tenantId);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public void importResidentTransactions(PmcYardiCredential yc, ResidentTransactions reversalTransactions) throws YardiServiceException, RemoteException {
        try {
            setMessageErrorHandler(new MessageErrorHandler() {
                @Override
                public boolean handle(Messages messages) throws YardiServiceException {
                    if (messages.hasErrorMessage(YardiHandledErrorMessages.unableToPostTerminalMessages)) {
                        throw new UnableToPostTerminalYardiServiceException(messages.getErrorMessage().getValue());
                    } else if (messages.hasErrorMessage(YardiHandledErrorMessages.errorMessage_NoAccess)) {
                        throw new YardiPropertyNoAccessException(messages.getErrorMessage().getValue());
                    }
                    return false;
                }
            });
            getStub(yc).importResidentTransactions(yc, reversalTransactions);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
    }

    @Override
    public ResidentTransactions getAllLeaseCharges(PmcYardiCredential yc, String propertyListCode, LogicalDate date) throws YardiServiceException,
            RemoteException {
        try {
            return getStub(yc).getAllLeaseCharges(yc, propertyListCode, date);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public ResidentTransactions getLeaseChargesForTenant(PmcYardiCredential yc, String propertyId, String tenantId, LogicalDate date)
            throws YardiServiceException, RemoteException {
        try {
            return getStub(yc).getLeaseChargesForTenant(yc, propertyId, tenantId, date);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

}
