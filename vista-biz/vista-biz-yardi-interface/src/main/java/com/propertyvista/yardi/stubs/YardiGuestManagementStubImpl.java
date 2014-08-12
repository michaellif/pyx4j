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
import javax.xml.stream.XMLStreamException;

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
import com.yardi.ws.operations.guestcard40.GetPropertyConfigurations;
import com.yardi.ws.operations.guestcard40.GetPropertyConfigurationsResponse;
import com.yardi.ws.operations.guestcard40.GetVersionNumber;
import com.yardi.ws.operations.guestcard40.GetVersionNumberResponse;
import com.yardi.ws.operations.guestcard40.GetYardiAgentsSourcesResults_Login;
import com.yardi.ws.operations.guestcard40.GetYardiAgentsSourcesResults_LoginResponse;
import com.yardi.ws.operations.guestcard40.GetYardiGuestActivity_Login;
import com.yardi.ws.operations.guestcard40.GetYardiGuestActivity_LoginResponse;
import com.yardi.ws.operations.guestcard40.GetYardiGuestActivity_Search;
import com.yardi.ws.operations.guestcard40.GetYardiGuestActivity_SearchResponse;
import com.yardi.ws.operations.guestcard40.GetYardiRentableItems_Login;
import com.yardi.ws.operations.guestcard40.GetYardiRentableItems_LoginResponse;
import com.yardi.ws.operations.guestcard40.ImportYardiGuest_Login;
import com.yardi.ws.operations.guestcard40.ImportYardiGuest_LoginResponse;
import com.yardi.ws.operations.guestcard40.Ping;
import com.yardi.ws.operations.guestcard40.PingResponse;
import com.yardi.ws.operations.guestcard40.XmlDoc_type0;
import com.yardi.ws.operations.ils.ImportApplication_Login;
import com.yardi.ws.operations.ils.ImportApplication_LoginResponse;
import com.yardi.ws.operations.ils.UnitAvailability_Login;
import com.yardi.ws.operations.ils.UnitAvailability_LoginResponse;
import com.yardi.ws.operations.ils.XmlDocument_type1;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiInterfaceType;
import com.propertyvista.yardi.beans.Properties;

class YardiGuestManagementStubImpl extends AbstractYardiStub implements YardiGuestManagementStub {

    private final static Logger log = LoggerFactory.getLogger(YardiGuestManagementStubImpl.class);

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
    public RentableItems getRentableItems(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        init(Action.GetYardiRentableItems);

        GetYardiRentableItems_Login request = new GetYardiRentableItems_Login();

        request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.ILSGuestCard, yc));

        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());

        request.setYardiPropertyId(propertyId);

        GetYardiRentableItems_LoginResponse response = getILSGuestCardService(yc).getYardiRentableItems_Login(request);
        String xml = response.getGetYardiRentableItems_LoginResult().getExtraElement().toString();

        return ensureResult(xml, RentableItems.class);
    }

    @Override
    public MarketingSources getYardiMarketingSources(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        init(Action.GetYardiMarketingSources);

        GetYardiAgentsSourcesResults_Login request = new GetYardiAgentsSourcesResults_Login();

        request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.ILSGuestCard, yc));

        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());

        request.setYardiPropertyId(propertyId);

        GetYardiAgentsSourcesResults_LoginResponse response = getILSGuestCardService(yc).getYardiAgentsSourcesResults_Login(request);
        String xml = response.getGetYardiAgentsSourcesResults_LoginResult().getExtraElement().toString();

        return ensureResult(xml, MarketingSources.class);
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

        UnitAvailability_LoginResponse response = getILSGuestCard20Service(yc).unitAvailability_Login(request);
        String xml = response.getUnitAvailability_LoginResult().getExtraElement().toString();

        return ensureResult(xml, PhysicalProperty.class);
    }

    @Override
    public LeadManagement getGuestActivity(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        init(Action.GetYardiGuestActivity);

        GetYardiGuestActivity_Login request = new GetYardiGuestActivity_Login();

        request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.ILSGuestCard, yc));

        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());

        request.setYardiPropertyId(propertyId);

        GetYardiGuestActivity_LoginResponse response = getILSGuestCardService(yc).getYardiGuestActivity_Login(request);
        String xml = response.getGetYardiGuestActivity_LoginResult().getExtraElement().toString();

        return ensureResult(xml, LeadManagement.class);
    }

    @Override
    public LeadManagement findGuest(PmcYardiCredential yc, String propertyId, String guestId) throws YardiServiceException, RemoteException {
        init(Action.GetYardiGuestSearch);

        GetYardiGuestActivity_Search request = new GetYardiGuestActivity_Search();

        request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.ILSGuestCard, yc));

        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());

        request.setYardiPropertyId(propertyId);
        request.setThirdPartyId(guestId);

        // all search fields are required
        request.setFirstName("");
        request.setLastName("");
        request.setEmailAddress("");
        request.setPhoneNumber("");
        request.setDateOfBirth("");
        request.setFederalId("");

        GetYardiGuestActivity_SearchResponse response = getILSGuestCardService(yc).getYardiGuestActivity_Search(request);
        String xml = response.getGetYardiGuestActivity_SearchResult().getExtraElement().toString();

        return ensureResult(xml, LeadManagement.class);
    }

    @Override
    public void importGuestInfo(PmcYardiCredential yc, LeadManagement leadInfo) throws YardiServiceException, RemoteException {
        init(Action.ImportGuestInfo);
        validateWriteAccess(yc);

        ImportYardiGuest_Login request = new ImportYardiGuest_Login();

        request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.ILSGuestCard, yc));

        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());

        try {
            String leadXml = MarshallUtil.marshall(leadInfo);
            log.debug(leadXml);
            XmlDoc_type0 xmlDoc = new XmlDoc_type0();
            OMElement element = AXIOMUtil.stringToOM(leadXml);
            xmlDoc.setExtraElement(element);
            request.setXmlDoc(xmlDoc);
        } catch (JAXBException e) {
            throw new Error(e);
        } catch (XMLStreamException e) {
            throw new Error(e);
        }

        ImportYardiGuest_LoginResponse response = getILSGuestCardService(yc).importYardiGuest_Login(request);
        String xml = response.getImportYardiGuest_LoginResult().getExtraElement().toString();

        ensureValid(xml);
    }

    @Override
    public void importApplication(PmcYardiCredential yc, LeaseApplication leaseApp) throws YardiServiceException, RemoteException {
        init(Action.ImportApplication);
        validateWriteAccess(yc);

        ImportApplication_Login request = new ImportApplication_Login();

        request.setInterfaceEntity(YardiConstants.ILS_INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.ILSGuestCard, yc));

        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());

        try {
            String leaseAppXml = MarshallUtil.marshall(leaseApp);
            XmlDocument_type1 xmlDoc = new XmlDocument_type1();
            OMElement element = AXIOMUtil.stringToOM(leaseAppXml);
            xmlDoc.setExtraElement(element);
            request.setXmlDocument(xmlDoc);
        } catch (JAXBException e) {
            throw new Error(e);
        } catch (XMLStreamException e) {
            throw new Error(e);
        }

        ImportApplication_LoginResponse response = getILSGuestCard20Service(yc).importApplication_Login(request);
        String xml = response.getImportApplication_LoginResult().getExtraElement().toString();

        ensureValid(xml);
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
