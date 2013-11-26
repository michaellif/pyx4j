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

import javax.xml.bind.JAXBException;

import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.ws.ItfILSGuestCard2_0;
import com.yardi.ws.ItfILSGuestCard2_0Stub;
import com.yardi.ws.operations.ils.GetPropertyConfigurations;
import com.yardi.ws.operations.ils.GetPropertyConfigurationsResponse;
import com.yardi.ws.operations.ils.UnitAvailability_Login;
import com.yardi.ws.operations.ils.UnitAvailability_LoginResponse;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiInterface;
import com.propertyvista.yardi.beans.Messages;
import com.propertyvista.yardi.beans.Properties;

public class YardiILSGuestCardStubImpl extends AbstractYardiStub implements YardiILSGuestCardStub {

    private final static Logger log = LoggerFactory.getLogger(YardiILSGuestCardStubImpl.class);

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        try {
            init(Action.GetPropertyConfigurations);

            GetPropertyConfigurations request = new GetPropertyConfigurations();

            request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.ILSGuestCard, yc));

            request.setUserName(yc.username().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());

            GetPropertyConfigurationsResponse response = getILSGuestCardService(yc).getPropertyConfigurations(request);
            if ((response == null) || (response.getGetPropertyConfigurationsResult() == null)
                    || (response.getGetPropertyConfigurationsResult().getExtraElement() == null)) {
                throw new YardiServiceException(SimpleMessageFormat.format(
                        "Yardi connection configuration error, Login error or database ''{0}'' do not exists on Yardi server", yc.database()));
            }
            String xml = response.getGetPropertyConfigurationsResult().getExtraElement().toString();

            log.debug("GetPropertyConfigurations Result: {}", xml);

            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    YardiLicense.handleVendorLicenseError(messages);
                    throw new YardiServiceException(messages.toString());
                } else {
                    log.info(messages.toString());
                }
            }

            Properties properties = MarshallUtil.unmarshal(Properties.class, xml);

            log.debug("\n--- GetPropertyConfigurations ---\n{}\n", properties);

            return properties;

        } catch (JAXBException e) {
            throw new Error(e);
        }
    }

    @Override
    public PhysicalProperty getPropertyMarketingInfo(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        try {
            init(Action.GetPropertyMarketingInfo);

            UnitAvailability_Login request = new UnitAvailability_Login();

            request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.ILSGuestCard, yc));

            request.setUserName(yc.username().getValue());
            request.setPassword(yc.password().number().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());

            request.setYardiPropertyId(propertyId);

            UnitAvailability_LoginResponse response = getILSGuestCardService(yc).unitAvailability_Login(request);
            if (response.getUnitAvailability_LoginResult() == null) {
                throw new Error("Received response is null");
            }

            String xml = response.getUnitAvailability_LoginResult().getExtraElement().toString();

            log.info("GetMarketingInfo Result: {}", xml);

            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    YardiLicense.handleVendorLicenseError(messages);
                    throw new YardiServiceException(messages.toString());
                } else {
                    log.info(messages.toString());
                }
            }
            PhysicalProperty property = MarshallUtil.unmarshal(PhysicalProperty.class, xml);

            log.debug("\n--- GetMarketingInfo ---\n{}\n", property);

            return property;
        } catch (Throwable e) {
            throw new YardiServiceException(e);
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
