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

import org.apache.axis2.AxisFault;

import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.ws.ItfILSGuestCard2_0;
import com.yardi.ws.ItfILSGuestCard2_0Stub;
import com.yardi.ws.operations.ils.GetPropertyConfigurations;
import com.yardi.ws.operations.ils.GetPropertyConfigurationsResponse;
import com.yardi.ws.operations.ils.GetVersionNumber;
import com.yardi.ws.operations.ils.GetVersionNumberResponse;
import com.yardi.ws.operations.ils.Ping;
import com.yardi.ws.operations.ils.PingResponse;
import com.yardi.ws.operations.ils.UnitAvailability_Login;
import com.yardi.ws.operations.ils.UnitAvailability_LoginResponse;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiInterfaceType;
import com.propertyvista.yardi.beans.Properties;

public class YardiILSGuestCardStubImpl extends AbstractYardiStub implements YardiILSGuestCardStub {

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        init(Action.GetPropertyConfigurations);

        GetPropertyConfigurations request = new GetPropertyConfigurations();

        request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.ILSGuestCard, yc));

        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());

        GetPropertyConfigurationsResponse response = getILSGuestCardService(yc).getPropertyConfigurations(request);
        String xml = response.getGetPropertyConfigurationsResult().getExtraElement().toString();
        return ensureResult(xml, Properties.class);
    }

    @Override
    public PhysicalProperty getPropertyMarketingInfo(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        init(Action.GetPropertyMarketingInfo);

        UnitAvailability_Login request = new UnitAvailability_Login();

        request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.ILSGuestCard, yc));

        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());

        request.setYardiPropertyId(propertyId);

        UnitAvailability_LoginResponse response = getILSGuestCardService(yc).unitAvailability_Login(request);
        String xml = response.getUnitAvailability_LoginResult().getExtraElement().toString();
        return ensureResult(xml, PhysicalProperty.class);
    }

    @Override
    public String ping(PmcYardiCredential yc) {
        try {
            init(Action.Ping);
            PingResponse response = getILSGuestCardService(yc).ping(new Ping());
            return response.getPingResult();
        } catch (RemoteException e) {
            throw new Error(e);
        }
    }

    @Override
    public void validate(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        // try to pull properties
        getPropertyConfigurations(yc);
    }

    @Override
    public String getPluginVersion(PmcYardiCredential yc) {
        try {
            init(Action.GetVersionNumber);
            GetVersionNumberResponse response = getILSGuestCardService(yc).getVersionNumber(new GetVersionNumber());
            return response.getGetVersionNumberResult();
        } catch (RemoteException e) {
            throw new Error(e);
        }

    }

    private ItfILSGuestCard2_0 getILSGuestCardService(PmcYardiCredential yc) throws AxisFault {
        ItfILSGuestCard2_0Stub serviceStub = new ItfILSGuestCard2_0Stub(ilsGuestCardServiceURL(yc));
        addMessageContextListener("ILSGuestCard", serviceStub, null);
        setTransportOptions(serviceStub, yc);
        return serviceStub;
    }

    private String ilsGuestCardServiceURL(PmcYardiCredential yc) {
        if (yc.ilsGuestCard20ServiceURL().isNull()) {
            return serviceWithPath(yc, "webservices/itfilsguestcard20.asmx");
        } else {
            return yc.ilsGuestCard20ServiceURL().getValue();
        }
    }
}
