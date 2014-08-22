/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 28, 2013
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

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiInterface;
import com.propertyvista.yardi.beans.Properties;

public interface YardiGuestManagementStub extends YardiInterface {

    Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException;

    AttachmentTypesAndChargeCodes getConfiguredAttachmentsAndCharges(PmcYardiCredential yc) throws YardiServiceException, RemoteException;

    RentableItems getRentableItems(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException;

    PhysicalProperty getPropertyMarketingInfo(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException;

    MarketingSources getYardiMarketingSources(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException;

    LeadManagement getGuestActivity(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException;

    LeadManagement findGuest(PmcYardiCredential yc, String propertyId, String guestId) throws YardiServiceException, RemoteException;

    LeaseApplication getApplication(PmcYardiCredential yc, String propertyId, String prospectId) throws YardiServiceException, RemoteException;

    void importGuestInfo(PmcYardiCredential yc, LeadManagement leadInfo) throws YardiServiceException, RemoteException;

    void importApplication(PmcYardiCredential yc, LeaseApplication leaseApp) throws YardiServiceException, RemoteException;
}
