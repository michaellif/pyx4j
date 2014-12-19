/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 9, 2013
 * @author stanp
 */
package com.propertyvista.yardi.mock.stub;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.guestcard40.AttachmentTypesAndChargeCodes;
import com.yardi.entity.guestcard40.LeadManagement;
import com.yardi.entity.guestcard40.MarketingSources;
import com.yardi.entity.guestcard40.RentableItems;
import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.entity.leaseapp30.LeaseApplication;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.mock.YardiMockServer;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;

public class YardiMockILSGuestCardStubImpl implements YardiILSGuestCardStub {

    private final static Logger log = LoggerFactory.getLogger(YardiMockILSGuestCardStubImpl.class);

    @Override
    public AttachmentTypesAndChargeCodes getConfiguredAttachmentsAndCharges(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RentableItems getRentableItems(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        return YardiMockUtils.dumpXml("getRentableItems", YardiMockServer.instance().getRentableItems(propertyId));
    }

    @Override
    public PhysicalProperty getPropertyMarketingInfo(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MarketingSources getYardiMarketingSources(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        return YardiMockUtils.dumpXml("getYMarketingSources", YardiMockServer.instance().getMarketingSources(propertyId));
    }

    @Override
    public LeadManagement getGuestActivity(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void importGuestInfo(PmcYardiCredential yc, LeadManagement leadInfo) throws YardiServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public void importApplication(PmcYardiCredential yc, LeaseApplication leaseApp) throws YardiServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public LeadManagement findGuest(PmcYardiCredential yc, String propertyId, String guestId) throws YardiServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String ping(PmcYardiCredential yc) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void validate(PmcYardiCredential yc) throws YardiServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getPluginVersion(PmcYardiCredential yc) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException {
        return YardiMockUtils.dumpXml("getPropertyConfigurations", YardiMockServer.instance().getPropertyConfigurations());
    }

    @Override
    public LeaseApplication getApplication(PmcYardiCredential yc, String propertyId, String prospectId) throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

}
