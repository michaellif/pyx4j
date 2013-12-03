/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 3, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.yardi.mock.stub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.guestcard40.LeadManagement;
import com.yardi.entity.guestcard40.MarketingSources;
import com.yardi.entity.guestcard40.RentableItems;
import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.entity.leaseapp30.LeaseApplication;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.stubs.YardiGuestManagementStub;

public class YardiMockGuestManagementStubImpl implements YardiGuestManagementStub {

    private final static Logger log = LoggerFactory.getLogger(YardiMockGuestManagementStubImpl.class);

    @Override
    public long getRequestsTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void logRecordedTracastions() {
        // TODO Auto-generated method stub
    }

    @Override
    public RentableItems getRentableItems(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PhysicalProperty getPropertyMarketingInfo(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MarketingSources getYardiMarketingSources(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        // TODO Auto-generated method stub
        return null;
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

}
