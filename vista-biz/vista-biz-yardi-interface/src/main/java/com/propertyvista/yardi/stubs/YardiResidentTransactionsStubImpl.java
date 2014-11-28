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
import com.yardi.ws.operations.transactions.GetVersionNumber;
import com.yardi.ws.operations.transactions.GetVersionNumberResponse;
import com.yardi.ws.operations.transactions.ImportResidentTransactions_Login;
import com.yardi.ws.operations.transactions.ImportResidentTransactions_LoginResponse;
import com.yardi.ws.operations.transactions.Ping;
import com.yardi.ws.operations.transactions.PingResponse;
import com.yardi.ws.operations.transactions.TransactionXml_type0;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiInterfaceType;
import com.propertyvista.yardi.beans.Properties;

class YardiResidentTransactionsStubImpl extends AbstractYardiStub implements YardiResidentTransactionsStub {

    private final static Logger log = LoggerFactory.getLogger(YardiResidentTransactionsStubImpl.class);

    @Override
    public String ping(PmcYardiCredential yc) {
        try {
            init(yc, Action.Ping);
            PingResponse response = getResidentTransactionsService(yc).ping(new Ping());
            return response.getPingResult();
        } catch (YardiServiceException | RemoteException e) {
            throw new Error(e);
        }
    }

    @Override
    public String getPluginVersion(PmcYardiCredential yc) {
        try {
            init(yc, Action.GetVersionNumber);
            GetVersionNumberResponse response = getResidentTransactionsService(yc).getVersionNumber(new GetVersionNumber());
            return response.getGetVersionNumberResult();
        } catch (YardiServiceException | RemoteException e) {
            throw new Error(e);
        }
    }

    @Override
    public void validate(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        // try to pull properties
        getPropertyConfigurations(yc);
    }

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        init(yc, Action.GetPropertyConfigurations);

        GetPropertyConfigurations request = new GetPropertyConfigurations();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.BillingAndPayments, yc));

        GetPropertyConfigurationsResponse response = getResidentTransactionsService(yc).getPropertyConfigurations(request);
        return ensureResult(response.getGetPropertyConfigurationsResult().getExtraElement(), Properties.class);
    }

    @Override
    public ResidentTransactions getAllResidentTransactions(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        init(yc, Action.GetResidentTransactions);

        GetResidentTransactions_Login request = new GetResidentTransactions_Login();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.BillingAndPayments, yc));
        request.setYardiPropertyId(propertyId);

        GetResidentTransactions_LoginResponse response = getResidentTransactionsService(yc).getResidentTransactions_Login(request);
        return ensureResult(response.getGetResidentTransactions_LoginResult().getExtraElement(), ResidentTransactions.class);
    }

    @Override
    public ResidentTransactions getResidentTransactionsForTenant(PmcYardiCredential yc, String propertyId, String tenantId) throws YardiServiceException,
            RemoteException {
        init(yc, Action.GetResidentTransaction);

        GetResidentTransaction_Login request = new GetResidentTransaction_Login();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.BillingAndPayments, yc));
        request.setYardiPropertyId(propertyId);
        request.setTenantId(tenantId);

        GetResidentTransaction_LoginResponse response = getResidentTransactionsService(yc).getResidentTransaction_Login(request);
        return ensureResult(response.getGetResidentTransaction_LoginResult().getExtraElement(), ResidentTransactions.class);
    }

    @Override
    public void importResidentTransactions(PmcYardiCredential yc, ResidentTransactions reversalTransactions) throws YardiServiceException, RemoteException {
        init(yc, Action.ImportResidentTransactions);

        ImportResidentTransactions_Login request = new ImportResidentTransactions_Login();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.BillingAndPayments, yc));

        try {
            String trXml = MarshallUtil.marshall(reversalTransactions);
            TransactionXml_type0 transactionXml = new TransactionXml_type0();
            OMElement element = AXIOMUtil.stringToOM(trXml);
            transactionXml.setExtraElement(element);
            request.setTransactionXml(transactionXml);
        } catch (JAXBException e) {
            throw new Error(e);
        } catch (XMLStreamException e) {
            throw new Error(e);
        }

        ImportResidentTransactions_LoginResponse response = getResidentTransactionsService(yc).importResidentTransactions_Login(request);
        ensureValid(response.getImportResidentTransactions_LoginResult().getExtraElement());
    }

    @Override
    public ResidentTransactions getAllLeaseCharges(PmcYardiCredential yc, String propertyListCode, LogicalDate date) throws YardiServiceException,
            RemoteException {
        init(yc, Action.GetResidentsLeaseCharges);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        GetResidentsLeaseCharges_Login request = new GetResidentsLeaseCharges_Login();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.BillingAndPayments, yc));
        request.setYardiPropertyId(propertyListCode);
        request.setPostMonth(calendar);

        GetResidentsLeaseCharges_LoginResponse response = getResidentTransactionsService(yc).getResidentsLeaseCharges_Login(request);
        return ensureResult(response.getGetResidentsLeaseCharges_LoginResult().getExtraElement(), ResidentTransactions.class);
    }

    @Override
    public ResidentTransactions getLeaseChargesForTenant(PmcYardiCredential yc, String propertyId, String tenantId, LogicalDate date)
            throws YardiServiceException, RemoteException {
        init(yc, Action.GetResidentLeaseCharges);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        GetResidentLeaseCharges_Login request = new GetResidentLeaseCharges_Login();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.BillingAndPayments, yc));
        request.setYardiPropertyId(propertyId);
        request.setTenantId(tenantId);
        request.setPostMonth(calendar);

        GetResidentLeaseCharges_LoginResponse response = getResidentTransactionsService(yc).getResidentLeaseCharges_Login(request);
        return ensureResult(response.getGetResidentLeaseCharges_LoginResult().getExtraElement(), ResidentTransactions.class);
    }

    private ItfResidentTransactions2_0 getResidentTransactionsService(PmcYardiCredential yc) {
        try {
            ItfResidentTransactions2_0Stub serviceStub = new ItfResidentTransactions2_0Stub(getResidentTransactionsServiceURL(yc));
            addMessageContextListener("ResidentTransactions", serviceStub, null);
            setTransportOptions(serviceStub, yc);
            return serviceStub;
        } catch (AxisFault e) {
            throw new Error(e);
        }
    }

    private String getResidentTransactionsServiceURL(PmcYardiCredential yc) {
        if (yc.residentTransactionsServiceURL().isNull()) {
            return serviceWithPath(yc, "webservices/itfresidenttransactions20.asmx");
        } else {
            return yc.residentTransactionsServiceURL().getValue();
        }
    }
}
