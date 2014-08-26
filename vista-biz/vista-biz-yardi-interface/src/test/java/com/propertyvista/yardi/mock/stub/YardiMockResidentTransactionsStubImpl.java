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
package com.propertyvista.yardi.mock.stub;

import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.TransactionLog;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.mock.YardiMockServer;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;

public class YardiMockResidentTransactionsStubImpl implements YardiResidentTransactionsStub {

    private final static Logger log = LoggerFactory.getLogger(YardiMockResidentTransactionsStubImpl.class);

    public static <T> T dumpXml(String contextName, T data) {
        try {
            String name = TransactionLog.log(TransactionLog.getNextNumber(),
                    contextName + "_" + new SimpleDateFormat("yyyy-MM-dd_HH_mm").format(SystemDateManager.getDate()), MarshallUtil.marshall(data), "xml");
            log.debug("log file created {}", name);
        } catch (JAXBException e) {
            log.error("writing data dump error", e);
        }
        return data;
    }

    @Override
    public String ping(PmcYardiCredential yc) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Properties getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException {
        return dumpXml("getPropertyConfigurations", YardiMockServer.instance().getPropertyConfigurations());
    }

    @Override
    public ResidentTransactions getAllResidentTransactions(PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        return dumpXml("getAllResidentTransactions", YardiMockServer.instance().getAllResidentTransactions(propertyId));
    }

    @Override
    public ResidentTransactions getResidentTransactionsForTenant(PmcYardiCredential yc, String propertyId, String tenantId) throws YardiServiceException {
        return YardiMockServer.instance().getResidentTransactionsForTenant(propertyId, tenantId);
    }

    @Override
    public void importResidentTransactions(PmcYardiCredential yc, ResidentTransactions reversalTransactions) throws YardiServiceException {
        YardiMockServer.instance().importResidentTransactions(reversalTransactions);
    }

    @Override
    public ResidentTransactions getAllLeaseCharges(PmcYardiCredential yc, String propertyId, LogicalDate date) throws YardiServiceException {
        return dumpXml("getAllLeaseCharges", YardiMockServer.instance().getAllLeaseCharges(propertyId));
    }

    @Override
    public ResidentTransactions getLeaseChargesForTenant(PmcYardiCredential yc, String propertyId, String tenantId, LogicalDate date)
            throws YardiServiceException {
        return dumpXml("getLeaseChargesForTenant", YardiMockServer.instance().getLeaseChargesForTenant(propertyId, tenantId));
    }

    @Override
    public void validate(PmcYardiCredential yc) throws YardiServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getPluginVersion(PmcYardiCredential yc) {
        // TODO Auto-generated method stub
        return null;
    }

}
