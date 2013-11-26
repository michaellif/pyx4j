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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.guestcard40.LeadManagement;
import com.yardi.entity.guestcard40.MarketingSources;
import com.yardi.entity.guestcard40.RentableItems;
import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.entity.leaseapp30.LeaseApplication;
import com.yardi.ws.ItfILSGuestCard;
import com.yardi.ws.ItfILSGuestCard2_0;
import com.yardi.ws.ItfILSGuestCard2_0Stub;
import com.yardi.ws.ItfILSGuestCardStub;
import com.yardi.ws.operations.guestcard40.GetYardiAgentsSourcesResults_Login;
import com.yardi.ws.operations.guestcard40.GetYardiAgentsSourcesResults_LoginResponse;
import com.yardi.ws.operations.guestcard40.GetYardiGuestActivity_Login;
import com.yardi.ws.operations.guestcard40.GetYardiGuestActivity_LoginResponse;
import com.yardi.ws.operations.guestcard40.GetYardiRentableItems_Login;
import com.yardi.ws.operations.guestcard40.GetYardiRentableItems_LoginResponse;
import com.yardi.ws.operations.guestcard40.ImportYardiGuest_Login;
import com.yardi.ws.operations.guestcard40.ImportYardiGuest_LoginResponse;
import com.yardi.ws.operations.guestcard40.XmlDoc_type0;
import com.yardi.ws.operations.ils.ImportApplication_Login;
import com.yardi.ws.operations.ils.ImportApplication_LoginResponse;
import com.yardi.ws.operations.ils.UnitAvailability_Login;
import com.yardi.ws.operations.ils.UnitAvailability_LoginResponse;
import com.yardi.ws.operations.ils.XmlDocument_type0;

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

public class YardiGuestManagementStubImpl extends AbstractYardiStub implements YardiGuestManagementStub {

    private final static Logger log = LoggerFactory.getLogger(YardiGuestManagementStubImpl.class);

    private final boolean testMode = true;

    @Override
    public RentableItems getRentableItems(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        try {
            init(Action.GetYardiRentableItems);

            GetYardiRentableItems_Login request = new GetYardiRentableItems_Login();

            request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.ILSGuestCard, yc));

            request.setUserName(yc.username().getValue());
            if (testMode) { // TODO
                request.setPassword(yc.password().number().getValue());
            } else {
                request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            }
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());

            request.setYardiPropertyId(propertyId);

            GetYardiRentableItems_LoginResponse response = getILSGuestCardService(yc).getYardiRentableItems_Login(request);
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

        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    @Override
    public MarketingSources getYardiMarketingSources(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        try {
            init(Action.GetYardiMarketingSources);

            GetYardiAgentsSourcesResults_Login request = new GetYardiAgentsSourcesResults_Login();

            request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.ILSGuestCard, yc));

            request.setUserName(yc.username().getValue());
            if (testMode) { // TODO
                request.setPassword(yc.password().number().getValue());
            } else {
                request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            }
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());

            request.setYardiPropertyId(propertyId);

            GetYardiAgentsSourcesResults_LoginResponse response = getILSGuestCardService(yc).getYardiAgentsSourcesResults_Login(request);
            if ((response == null) || (response.getGetYardiAgentsSourcesResults_LoginResult() == null)
                    || (response.getGetYardiAgentsSourcesResults_LoginResult().getExtraElement() == null)) {
                throw new YardiServiceException(SimpleMessageFormat.format(
                        "Yardi connection configuration error, Login error or database ''{0}'' do not exists on Yardi server", yc.database()));
            }

            OMElement root = response.getGetYardiAgentsSourcesResults_LoginResult().getExtraElement();
            String xml = root.toString();

            log.debug("GetYardiMarketingSources Result: {}", xml);

            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    YardiLicense.handleVendorLicenseError(messages);
                    throw new YardiServiceException(messages.toString());
                } else {
                    log.info(messages.toString());
                }
            }

            MarketingSources marketingSources = MarshallUtil.unmarshal(MarketingSources.class, xml);

            log.debug("\n--- GetYardiMarketingSources ---\n{}\n", marketingSources);

            return marketingSources;

        } catch (Throwable e) {
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
            if (testMode) { // TODO
                request.setPassword(yc.password().number().getValue());
            } else {
                request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            }
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());

            request.setYardiPropertyId(propertyId);

            UnitAvailability_LoginResponse response = getILSGuestCard20Service(yc).unitAvailability_Login(request);
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
    public LeadManagement getGuestActivity(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        try {
            init(Action.GetYardiGuestActivity);

            GetYardiGuestActivity_Login request = new GetYardiGuestActivity_Login();

            request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.ILSGuestCard, yc));

            request.setUserName(yc.username().getValue());
            if (testMode) { // TODO
                request.setPassword(yc.password().number().getValue());
            } else {
                request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            }
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());

            request.setYardiPropertyId(propertyId);

            GetYardiGuestActivity_LoginResponse response = getILSGuestCardService(yc).getYardiGuestActivity_Login(request);
            if (response.getGetYardiGuestActivity_LoginResult() == null) {
                throw new Error("Received response is null");
            }

            String xml = response.getGetYardiGuestActivity_LoginResult().getExtraElement().toString();

            log.info("GetYardiGuestActivity Result: {}", xml);

            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    YardiLicense.handleVendorLicenseError(messages);
                    throw new YardiServiceException(messages.toString());
                } else {
                    log.info(messages.toString());
                }
            }
            LeadManagement guestInfo = MarshallUtil.unmarshal(LeadManagement.class, xml);

            log.debug("\n--- GetYardiGuestActivity ---\n{}\n", guestInfo);

            return guestInfo;
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
            if (testMode) { // TODO
                request.setPassword(yc.password().number().getValue());
            } else {
                request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            }
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());

            String leadXml = MarshallUtil.marshall(leadInfo);
            log.debug(leadXml);
            XmlDoc_type0 xmlDoc = new XmlDoc_type0();
            OMElement element = AXIOMUtil.stringToOM(leadXml);
            xmlDoc.setExtraElement(element);
            request.setXmlDoc(xmlDoc);

            ImportYardiGuest_LoginResponse response = getILSGuestCardService(yc).importYardiGuest_Login(request);
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

    @Override
    public void importApplication(PmcYardiCredential yc, LeaseApplication leaseApp) throws YardiServiceException {
        try {
            init(Action.ImportApplication);

            ImportApplication_Login request = new ImportApplication_Login();

            request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.ILSGuestCard, yc));

            request.setUserName(yc.username().getValue());
            if (testMode) { // TODO
                request.setPassword(yc.password().number().getValue());
            } else {
                request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            }
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());

            String leaseAppXml = MarshallUtil.marshall(leaseApp);
            log.debug(leaseAppXml);
            XmlDocument_type0 xmlDoc = new XmlDocument_type0();
            OMElement element = AXIOMUtil.stringToOM(leaseAppXml);
            xmlDoc.setExtraElement(element);
            request.setXmlDocument(xmlDoc);

            ImportApplication_LoginResponse response = getILSGuestCard20Service(yc).importApplication_Login(request);
            if ((response == null) || (response.getImportApplication_LoginResult() == null)
                    || (response.getImportApplication_LoginResult().getExtraElement() == null)) {
                throw new YardiServiceException("importResidentTransactions received NULL response");
            }
            String xml = response.getImportApplication_LoginResult().getExtraElement().toString();

            log.debug("ImportApplication: {}", xml);

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

    private ItfILSGuestCard getILSGuestCardService(PmcYardiCredential yc) throws AxisFault {
        ItfILSGuestCardStub serviceStub = new ItfILSGuestCardStub(ilsGuestCardServiceURL(yc));
        addMessageContextListener("ILSGuestCard", serviceStub, null);
        setTransportOptions(serviceStub, yc);
        return serviceStub;
    }

    private ItfILSGuestCard2_0 getILSGuestCard20Service(PmcYardiCredential yc) throws AxisFault {
        ItfILSGuestCard2_0Stub serviceStub = new ItfILSGuestCard2_0Stub(ilsGuestCard20ServiceURL(yc));
        addMessageContextListener("ILSGuestCard20", serviceStub, null);
        setTransportOptions(serviceStub, yc);
        return serviceStub;
    }

    private String ilsGuestCardServiceURL(PmcYardiCredential yc) {
        if (yc.ilsGuestCardServiceURL().isNull()) {
            return serviceWithPath(yc, "webservices/itfilsguestcard.asmx");
        } else {
            return yc.ilsGuestCardServiceURL().getValue();
        }
    }

    private String ilsGuestCard20ServiceURL(PmcYardiCredential yc) {
        if (yc.ilsGuestCard20ServiceURL().isNull()) {
            return serviceWithPath(yc, "webservices/itfilsguestcard20.asmx");
        } else {
            return yc.ilsGuestCard20ServiceURL().getValue();
        }
    }
}
