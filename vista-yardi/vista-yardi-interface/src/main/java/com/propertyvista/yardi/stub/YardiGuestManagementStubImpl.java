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
package com.propertyvista.yardi.stub;

import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.guestcard40.LeadManagement;
import com.yardi.entity.guestcard40.RentableItems;
import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.ws.ItfILSGuestCard;
import com.yardi.ws.ItfILSGuestCardStub;
import com.yardi.ws.operations.guestcard40.GetYardiRentableItems_Login;
import com.yardi.ws.operations.guestcard40.GetYardiRentableItems_LoginResponse;
import com.yardi.ws.operations.guestcard40.ImportYardiGuest_Login;
import com.yardi.ws.operations.guestcard40.ImportYardiGuest_LoginResponse;
import com.yardi.ws.operations.guestcard40.UnitAvailability_Login;
import com.yardi.ws.operations.guestcard40.UnitAvailability_LoginResponse;
import com.yardi.ws.operations.guestcard40.XmlDoc_type0;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiInterface;
import com.propertyvista.yardi.bean.Messages;

public class YardiGuestManagementStubImpl extends AbstractYardiStub implements YardiGuestManagementStub {

    private final static Logger log = LoggerFactory.getLogger(YardiGuestManagementStubImpl.class);

    @Override
    public RentableItems getRentableItems(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        try {
            init(Action.GetYardiRentableItems);

            GetYardiRentableItems_Login request = new GetYardiRentableItems_Login();

            request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.ILSGuestCard, yc));

            request.setUserName(yc.username().getValue());
            request.setPassword(yc.password().number().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());

            GetYardiRentableItems_LoginResponse response = getGuestManagementService(yc).getYardiRentableItems_Login(request);
            if ((response == null) || (response.getGetYardiRentableItems_LoginResult() == null)
                    || (response.getGetYardiRentableItems_LoginResult().getExtraElement() == null)) {
                throw new YardiServiceException(SimpleMessageFormat.format(
                        "Yardi connection configuration error, Login error or database ''{0}'' do not exists on Yardi server", yc.database()));
            }
            String xml = response.getGetYardiRentableItems_LoginResult().getExtraElement().toString();

            log.debug("GetYardiRentableItems Result: {}", xml);

            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    YardiLicense.handleVendorLicenseError(messages);
                    throw new YardiServiceException(messages.toString());
                } else {
                    log.info(messages.toString());
                }
            }

            RentableItems rentableItems = MarshallUtil.unmarshal(RentableItems.class, xml);

            log.debug("\n--- GetPropertyConfigurations ---\n{}\n", rentableItems);

            return rentableItems;

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

            UnitAvailability_LoginResponse response = getGuestManagementService(yc).unitAvailability_Login(request);
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

    @Override
    public void importGuestInfo(PmcYardiCredential yc, LeadManagement leadInfo) throws YardiServiceException {
        try {
            init(Action.ImportGuestInfo);

            ImportYardiGuest_Login request = new ImportYardiGuest_Login();

            request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.ILSGuestCard, yc));

            request.setUserName(yc.username().getValue());
            request.setPassword(yc.password().number().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());

            String leadXml = MarshallUtil.marshall(leadInfo);
            log.debug(leadXml);
            XmlDoc_type0 xmlDoc = new XmlDoc_type0();
            OMElement element = AXIOMUtil.stringToOM(leadXml);
            xmlDoc.setExtraElement(element);
            request.setXmlDoc(xmlDoc);

            ImportYardiGuest_LoginResponse response = getGuestManagementService(yc).importYardiGuest_Login(request);
            if ((response == null) || (response.getImportYardiGuest_LoginResult() == null)
                    || (response.getImportYardiGuest_LoginResult().getExtraElement() == null)) {
                throw new YardiServiceException("importResidentTransactions received NULL response");
            }
            String xml = response.getImportYardiGuest_LoginResult().getExtraElement().toString();

            log.debug("ImportYardiGuest: {}", xml);

            Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
            if (messages.isError()) {
                YardiLicense.handleVendorLicenseError(messages);
                throw new YardiServiceException(messages.toString());
            } else {
                log.info(messages.toString());
            }
        } catch (Throwable e) {
            throw new YardiServiceException(e);
        }
    }

    private ItfILSGuestCard getGuestManagementService(PmcYardiCredential yc) throws AxisFault {
        ItfILSGuestCardStub serviceStub = new ItfILSGuestCardStub(guestCardServiceURL(yc));
        addMessageContextListener("GuestManagement", serviceStub, null);
        setTransportOptions(serviceStub, yc);
        return serviceStub;
    }

    private String guestCardServiceURL(PmcYardiCredential yc) {
        if (yc.ilsGuestCardServiceURL().isNull()) {
            return serviceWithPath(yc, "webservices/itfilsguestcard.asmx");
        } else {
            return yc.ilsGuestCardServiceURL().getValue();
        }
    }
}
