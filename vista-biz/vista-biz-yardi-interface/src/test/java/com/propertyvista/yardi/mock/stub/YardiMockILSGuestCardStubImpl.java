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
 * @version $Id$
 */
package com.propertyvista.yardi.mock.stub;

import java.rmi.RemoteException;

import com.yardi.entity.ils.PhysicalProperty;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;

public class YardiMockILSGuestCardStubImpl implements YardiILSGuestCardStub {

    @Override
    public void logRecordedTracastions() {
    }

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PhysicalProperty getPropertyMarketingInfo(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String ping(PmcYardiCredential yc) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void validate(PmcYardiCredential yc) throws RemoteException, YardiServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getPluginVersion(PmcYardiCredential yc) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

}
