/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 5, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.stubs;

import java.rmi.RemoteException;

import com.yardi.entity.guestcard40.AttachmentTypesAndChargeCodes;
import com.yardi.entity.guestcard40.LeadManagement;
import com.yardi.entity.guestcard40.MarketingSources;
import com.yardi.entity.guestcard40.RentableItems;
import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.entity.leaseapp30.LeaseApplication;

import com.propertyvista.biz.system.yardi.YardiInterfaceNotConfiguredException;
import com.propertyvista.biz.system.yardi.YardiPropertyNoAccessException;
import com.propertyvista.biz.system.yardi.YardiProspectNotEditableException;
import com.propertyvista.biz.system.yardi.YardiResponseException;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.beans.Messages;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.services.YardiHandledErrorMessages;

class YardiILSGuestCardStubProxy extends YardiAbstractStubProxy implements YardiILSGuestCardStub {

    YardiILSGuestCardStubProxy() {
        setMessageErrorHandler(noPropertyAccessHandler);
    }

    private YardiILSGuestCardStub getStub(PmcYardiCredential yc) {
        return getStubInstance(YardiILSGuestCardStub.class, yc);
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
        try {
            getStub(yc).validate(yc);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
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
    public AttachmentTypesAndChargeCodes getConfiguredAttachmentsAndCharges(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        try {
            return getStub(yc).getConfiguredAttachmentsAndCharges(yc);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public RentableItems getRentableItems(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        try {
            return getStub(yc).getRentableItems(yc, propertyId);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public PhysicalProperty getPropertyMarketingInfo(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        try {
            return getStub(yc).getPropertyMarketingInfo(yc, propertyId);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public MarketingSources getYardiMarketingSources(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        try {
            setMessageErrorHandler(new MessageErrorHandler() {
                @Override
                public boolean handle(Messages messages) throws YardiServiceException {
                    if (messages.hasErrorMessage(YardiHandledErrorMessages.errorMessage_InterfaceNotConfigured)) {
                        throw new YardiInterfaceNotConfiguredException(messages.getErrorMessage().getValue());
                    } else if (messages.hasErrorMessage(YardiHandledErrorMessages.errorMessage_NoAccess)) {
                        throw new YardiPropertyNoAccessException(messages.getErrorMessage().getValue());
                    }
                    return false;
                }
            });
            return getStub(yc).getYardiMarketingSources(yc, propertyId);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public LeadManagement getGuestActivity(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        try {
            return getStub(yc).getGuestActivity(yc, propertyId);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public LeaseApplication getApplication(PmcYardiCredential yc, String propertyId, String prospectId) throws YardiServiceException, RemoteException {
        try {
            setDataErrorHandler(new DataErrorHandler() {
                @Override
                public void handle(String xml) throws YardiServiceException {
                    for (String regex : new String[] { ".*<Error>(.*)</Error>.*" }) {
                        if (xml.matches(regex) && ignoreErrorMessage(xml.replaceFirst(regex, "$1"))) {
                            return;
                        }
                    }
                    throw new YardiServiceException(GENERIC_YARDI_ERROR);
                }

                private boolean ignoreErrorMessage(String message) throws YardiServiceException {
                    String lcMessage = message == null ? null : message.toLowerCase();
                    // we don't consider this an error
                    return lcMessage == null || lcMessage.contains("invalid prospect id");
                }
            });
            return getStub(yc).getApplication(yc, propertyId, prospectId);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public LeadManagement findGuest(PmcYardiCredential yc, String propertyId, String guestId) throws YardiServiceException, RemoteException {
        try {
            setMessageErrorHandler(new MessageErrorHandler() {
                @Override
                public boolean handle(Messages messages) throws YardiServiceException {
                    if (messages.hasErrorMessage(YardiHandledErrorMessages.errorMessage_GuestNotFound)) {
                        return true;
                    } else if (messages.hasErrorMessage(YardiHandledErrorMessages.errorMessage_NoAccess)) {
                        throw new YardiPropertyNoAccessException(messages.getErrorMessage().getValue());
                    }
                    return false;
                }
            });
            return getStub(yc).findGuest(yc, propertyId, guestId);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
        return null;
    }

    @Override
    public void importGuestInfo(PmcYardiCredential yc, LeadManagement leadInfo) throws YardiServiceException, RemoteException {
        try {
            setMessageErrorHandler(new MessageErrorHandler() {
                @Override
                public boolean handle(Messages messages) throws YardiServiceException {
                    if (messages.hasErrorMessage(YardiHandledErrorMessages.errorMessage_ProspectNotEditable)) {
                        throw new YardiProspectNotEditableException(messages.getErrorMessage().getValue());
                    } else if (messages.hasErrorMessage(YardiHandledErrorMessages.errorMessage_NoAccess)) {
                        throw new YardiPropertyNoAccessException(messages.getErrorMessage().getValue());
                    }
                    return false;
                }
            });
            getStub(yc).importGuestInfo(yc, leadInfo);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
    }

    @Override
    public void importApplication(PmcYardiCredential yc, LeaseApplication leaseApp) throws YardiServiceException, RemoteException {
        try {
            getStub(yc).importApplication(yc, leaseApp);
        } catch (YardiResponseException e) {
            validateResponseXml(e.getResponse());
        }
    }
}
