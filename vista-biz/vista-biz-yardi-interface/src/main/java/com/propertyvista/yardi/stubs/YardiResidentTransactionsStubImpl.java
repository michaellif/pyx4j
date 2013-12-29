/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.stubs;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.ws.ItfResidentTransactions2_0;
import com.yardi.ws.ItfResidentTransactions2_0Stub;
import com.yardi.ws.operations.transactions.GetPropertyConfigurations;
import com.yardi.ws.operations.transactions.GetPropertyConfigurationsResponse;
import com.yardi.ws.operations.transactions.GetResidentLeaseCharges_Login;
import com.yardi.ws.operations.transactions.GetResidentLeaseCharges_LoginResponse;
import com.yardi.ws.operations.transactions.GetResidentTransaction_Login;
import com.yardi.ws.operations.transactions.GetResidentTransaction_LoginResponse;
import com.yardi.ws.operations.transactions.GetResidentTransactions_Login;
import com.yardi.ws.operations.transactions.GetResidentTransactions_LoginResponse;
import com.yardi.ws.operations.transactions.GetResidentsLeaseCharges_Login;
import com.yardi.ws.operations.transactions.GetResidentsLeaseCharges_LoginResponse;
import com.yardi.ws.operations.transactions.GetUnitInformation_Login;
import com.yardi.ws.operations.transactions.GetUnitInformation_LoginResponse;
import com.yardi.ws.operations.transactions.ImportResidentTransactions_Login;
import com.yardi.ws.operations.transactions.ImportResidentTransactions_LoginResponse;
import com.yardi.ws.operations.transactions.Ping;
import com.yardi.ws.operations.transactions.PingResponse;
import com.yardi.ws.operations.transactions.TransactionXml_type1;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.UnableToPostTerminalYardiServiceException;
import com.propertyvista.biz.system.YardiPropertyNoAccessException;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiInterface;
import com.propertyvista.yardi.beans.Messages;
import com.propertyvista.yardi.beans.Properties;

public class YardiResidentTransactionsStubImpl extends AbstractYardiStub implements YardiResidentTransactionsStub {

    private final static Logger log = LoggerFactory.getLogger(YardiResidentTransactionsStubImpl.class);

    private static final String errorMessage_NoAccess = "Invalid or no access to Yardi Property";

    private static final String errorMessage_TenantNotFound = "No tenants exist with the given search criteria";

    //-- payment reversal post messages

    private static final String errorMessage_AlreadyNSF1 = "May not  NSF  a receipt that has been NSF";

    private static final String errorMessage_AlreadyNSF2 = "May not  reverse  a receipt that has been NSF";

    private static final String errorMessage_AlreadyReversed = "Receipt has already been reversed";

    private static final String errorMessage_PostMonthAccess1 = "Cannot  NSF  a receipt whose post month is outside your allowable range";

    private static final String errorMessage_PostMonthAccess2 = "Cannot  reverse  a receipt whose post month is outside your allowable range";

    private static final String[] unableToPostTerminalMessages = new String[] { errorMessage_AlreadyNSF1, errorMessage_AlreadyNSF2,
            errorMessage_AlreadyReversed, errorMessage_PostMonthAccess1, errorMessage_PostMonthAccess2 };

    @Override
    public String ping(PmcYardiCredential yc) throws RemoteException {
        init(Action.Ping);
        PingResponse pr = getResidentTransactionsService(yc).ping(new Ping());
        return pr.getPingResult();
    }

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        try {
            init(Action.GetPropertyConfigurations);

            GetPropertyConfigurations request = new GetPropertyConfigurations();
            request.setUserName(yc.username().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());
            request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.BillingAndPayments, yc));

            GetPropertyConfigurationsResponse response = getResidentTransactionsService(yc).getPropertyConfigurations(request);
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
    public ResidentTransactions getAllResidentTransactions(PmcYardiCredential yc, String propertyId) throws YardiServiceException,
            YardiPropertyNoAccessException, RemoteException {
        boolean success = false;
        try {

            init(Action.GetResidentTransactions);

            GetResidentTransactions_Login request = new GetResidentTransactions_Login();
            request.setUserName(yc.username().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());
            request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.BillingAndPayments, yc));
            request.setYardiPropertyId(propertyId);

            GetResidentTransactions_LoginResponse response = getResidentTransactionsService(yc).getResidentTransactions_Login(request);
            if ((response == null) || (response.getGetResidentTransactions_LoginResult() == null)) {
                throw new YardiServiceException(SimpleMessageFormat.format(
                        "Yardi connection configuration error, Login error or database ''{0}'' or Property Id ''{1}'' do not exists on Yardi server",
                        yc.database(), propertyId));
            }
            String xml = response.getGetResidentTransactions_LoginResult().getExtraElement().toString();

            log.debug("GetResidentTransactions: {}", xml);
            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    if (messages.hasErrorMessage(errorMessage_NoAccess)) {
                        throw new YardiPropertyNoAccessException(messages.getErrorMessage().getValue());
                    } else if (messages.hasErrorMessage(errorMessage_TenantNotFound)) {
                        success = true;
                        return null;
                    } else {
                        YardiLicense.handleVendorLicenseError(messages);
                        throw new YardiServiceException(SimpleMessageFormat.format("{0}; PropertyId {1}", messages.toString(), propertyId));
                    }
                } else {
                    log.info(messages.toString());
                }
            }

            ResidentTransactions transactions = MarshallUtil.unmarshal(ResidentTransactions.class, xml);
            success = true;
            return transactions;
        } catch (JAXBException e) {
            throw new Error(e);
        } finally {
            if (!success) {
                log.warn("Yardi transaction recorded at {}", recordedTracastionsLogs);
            }
        }
    }

    @Override
    public ResidentTransactions getResidentTransactionsForTenant(PmcYardiCredential yc, String propertyId, String tenantId) throws YardiServiceException,
            RemoteException {
        boolean success = false;
        try {
            init(Action.GetResidentTransaction);

            GetResidentTransaction_Login request = new GetResidentTransaction_Login();
            request.setUserName(yc.username().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());
            request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.BillingAndPayments, yc));
            request.setYardiPropertyId(propertyId);
            request.setTenantId(tenantId);

            GetResidentTransaction_LoginResponse response = getResidentTransactionsService(yc).getResidentTransaction_Login(request);
            if ((response == null) || (response.getGetResidentTransaction_LoginResult() == null)
                    || (response.getGetResidentTransaction_LoginResult().getExtraElement() == null)) {
                throw new YardiServiceException(SimpleMessageFormat.format(
                        "Yardi connection configuration error, Login error or database ''{0}'' do not exists on Yardi server; PropertyId {1}, TenantId {2}",
                        yc.database(), propertyId, tenantId));
            }
            String xml = response.getGetResidentTransaction_LoginResult().getExtraElement().toString();

            log.debug("GetResidentTransaction: {}", xml);
            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    YardiLicense.handleVendorLicenseError(messages);
                    throw new YardiServiceException(SimpleMessageFormat.format("{0}; PropertyId {1}, TenantId {2}", messages.toString(), propertyId, tenantId));
                } else {
                    log.info(messages.toString());
                }
            }
            ResidentTransactions transactions = MarshallUtil.unmarshal(ResidentTransactions.class, xml);
            success = true;
            return transactions;
        } catch (JAXBException e) {
            throw new Error(e);
        } finally {
            if (!success) {
                log.warn("Yardi transaction recorded at {}", recordedTracastionsLogs);
            }
        }
    }

    @Override
    public void importResidentTransactions(PmcYardiCredential yc, ResidentTransactions reversalTransactions) throws YardiServiceException, RemoteException {
        boolean success = false;
        try {
            init(Action.ImportResidentTransactions);

            ImportResidentTransactions_Login request = new ImportResidentTransactions_Login();
            request.setUserName(yc.username().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());
            request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.BillingAndPayments, yc));

            String trXml = MarshallUtil.marshall(reversalTransactions);
            log.debug(trXml);
            TransactionXml_type1 transactionXml = new TransactionXml_type1();
            OMElement element = AXIOMUtil.stringToOM(trXml);
            transactionXml.setExtraElement(element);
            request.setTransactionXml(transactionXml);

            ImportResidentTransactions_LoginResponse response = getResidentTransactionsService(yc).importResidentTransactions_Login(request);
            if ((response == null) || (response.getImportResidentTransactions_LoginResult() == null)
                    || (response.getImportResidentTransactions_LoginResult().getExtraElement() == null)) {
                throw new YardiServiceException("importResidentTransactions received NULL response");
            }
            String xml = response.getImportResidentTransactions_LoginResult().getExtraElement().toString();

            log.debug("ImportResidentTransactions: {}", xml);

            Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
            if (messages.isError()) {
                YardiLicense.handleVendorLicenseError(messages);
                if (messages.hasErrorMessage(unableToPostTerminalMessages)) {
                    throw new UnableToPostTerminalYardiServiceException(messages.getPrettyErrorMessageText());
                } else {
                    throw new YardiServiceException(messages.toString());
                }
            } else {
                log.debug(messages.toString());
            }
            success = true;
        } catch (JAXBException e) {
            throw new Error(e);
        } catch (XMLStreamException e) {
            throw new Error(e);
        } finally {
            if (!success) {
                log.warn("Yardi transaction recorded at {}", recordedTracastionsLogs);
            }
        }
    }

    @Override
    public void getUnitInformation(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        boolean success = false;
        try {
            init(Action.GetUnitInformation);

            GetUnitInformation_Login request = new GetUnitInformation_Login();
            request.setUserName(yc.username().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());
            request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.BillingAndPayments, yc));
            request.setYardiPropertyId(propertyId);

            GetUnitInformation_LoginResponse response = getResidentTransactionsService(yc).getUnitInformation_Login(request);
            if ((response == null) || (response.getGetUnitInformation_LoginResult() == null)
                    || (response.getGetUnitInformation_LoginResult().getExtraElement() == null)) {
                throw new YardiServiceException("getUnitInformation_Login received NULL response");
            }
            String xml = response.getGetUnitInformation_LoginResult().getExtraElement().toString();

            log.debug("GetUnitInformation: {}", xml);
            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    YardiLicense.handleVendorLicenseError(messages);
                    throw new YardiServiceException(SimpleMessageFormat.format("{0}; PropertyId {1}", messages.toString(), propertyId));
                } else {
                    log.debug(messages.toString());
                }
            }
            //TODO
//            ResidentTransactions transactions = MarshallUtil.unmarshal(ResidentTransactions.class, xml);
//            return transactions;

            success = true;
        } catch (JAXBException e) {
            throw new Error(e);
        } finally {
            if (!success) {
                log.warn("Yardi transaction recorded at {}", recordedTracastionsLogs);
            }
        }
    }

    @Override
    public ResidentTransactions getAllLeaseCharges(PmcYardiCredential yc, String propertyListCode, LogicalDate date) throws YardiServiceException,
            RemoteException, YardiPropertyNoAccessException {
        boolean success = false;
        try {
            init(Action.GetResidentsLeaseCharges);

            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);

            GetResidentsLeaseCharges_Login request = new GetResidentsLeaseCharges_Login();
            request.setUserName(yc.username().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());
            request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.BillingAndPayments, yc));
            request.setYardiPropertyId(propertyListCode);
            request.setPostMonth(calendar);

            GetResidentsLeaseCharges_LoginResponse response = getResidentTransactionsService(yc).getResidentsLeaseCharges_Login(request);
            if ((response == null) || (response.getGetResidentsLeaseCharges_LoginResult() == null)
                    || (response.getGetResidentsLeaseCharges_LoginResult().getExtraElement() == null)) {
                throw new YardiServiceException(SimpleMessageFormat.format("getResidentsLeaseCharges received NULL response; PropertyListCode {0}, Date {1}",
                        propertyListCode, date));
            }
            String xml = response.getGetResidentsLeaseCharges_LoginResult().getExtraElement().toString();

            log.debug("GetResidentsLeaseCharges: {}", xml);
            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    if (messages.hasErrorMessage(errorMessage_NoAccess)) {
                        throw new YardiPropertyNoAccessException(messages.getErrorMessage().getValue());
                    } else if (messages.hasErrorMessage(errorMessage_NoAccess)) {
                        throw new YardiPropertyNoAccessException(messages.getErrorMessage().getValue());
                    } else {
                        YardiLicense.handleVendorLicenseError(messages);
                        throw new YardiServiceException(SimpleMessageFormat.format("{0}; PropertyListCode {1}, Date {2}", messages.toString(),
                                propertyListCode, date));
                    }
                } else {
                    log.debug(messages.toString());
                }
            }

            ResidentTransactions transactions = MarshallUtil.unmarshal(ResidentTransactions.class, xml);
            success = true;
            return transactions;
        } catch (JAXBException e) {
            throw new Error(e);
        } finally {
            if (!success) {
                log.warn("Yardi transaction recorded at {}", recordedTracastionsLogs);
            }
        }
    }

    @Override
    public ResidentTransactions getLeaseChargesForTenant(PmcYardiCredential yc, String propertyId, String tenantId, LogicalDate date)
            throws YardiServiceException, RemoteException, YardiResidentNoTenantsExistException {
        boolean success = false;
        try {
            init(Action.GetResidentLeaseCharges);

            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);

            GetResidentLeaseCharges_Login request = new GetResidentLeaseCharges_Login();
            request.setUserName(yc.username().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());
            request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterface.BillingAndPayments, yc));
            request.setYardiPropertyId(propertyId);
            request.setTenantId(tenantId);
            request.setPostMonth(calendar);

            GetResidentLeaseCharges_LoginResponse response = getResidentTransactionsService(yc).getResidentLeaseCharges_Login(request);
            if ((response == null) || (response.getGetResidentLeaseCharges_LoginResult() == null)
                    || (response.getGetResidentLeaseCharges_LoginResult().getExtraElement() == null)) {
                throw new YardiServiceException(SimpleMessageFormat.format(
                        "getResidentsLeaseCharges received NULL response; PropertyId {0}, TenantId {1}, Date {2}", propertyId, tenantId, date));
            }
            String xml = response.getGetResidentLeaseCharges_LoginResult().getExtraElement().toString();

            log.debug("GetResidentLeaseCharges: {}", xml);
            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    if (messages.hasErrorMessage(errorMessage_TenantNotFound)) {
                        throw new YardiResidentNoTenantsExistException(messages.getErrorMessage().getValue());
                    } else {
                        YardiLicense.handleVendorLicenseError(messages);
                        throw new YardiServiceException(SimpleMessageFormat.format("{0}; PropertyId {1}, TenantId {2}, Date {3}", messages.toString(),
                                propertyId, tenantId, date));
                    }
                } else {
                    log.info(messages.toString());
                }
            }

            ResidentTransactions transactions = MarshallUtil.unmarshal(ResidentTransactions.class, xml);
            success = true;
            return transactions;
        } catch (JAXBException e) {
            throw new Error(e);
        } finally {
            if (!success) {
                log.warn("Yardi transaction recorded at {}", recordedTracastionsLogs);
            }
        }
    }

    private ItfResidentTransactions2_0 getResidentTransactionsService(PmcYardiCredential yc) throws AxisFault {
        ItfResidentTransactions2_0Stub serviceStub = new ItfResidentTransactions2_0Stub(getResidentTransactionsServiceURL(yc));
        addMessageContextListener("ResidentTransactions", serviceStub, null);
        setTransportOptions(serviceStub, yc);
        return serviceStub;
    }

    private String getResidentTransactionsServiceURL(PmcYardiCredential yc) {
        if (yc.residentTransactionsServiceURL().isNull()) {
            return serviceWithPath(yc, "webservices/itfresidenttransactions20.asmx");
        } else {
            return yc.residentTransactionsServiceURL().getValue();
        }
    }

}
