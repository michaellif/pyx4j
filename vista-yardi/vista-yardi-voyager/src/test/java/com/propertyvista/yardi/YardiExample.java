/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.yardi.bean.out.Charge;
import com.propertyvista.yardi.bean.out.Detail;

public class YardiExample {

    private final static Logger log = LoggerFactory.getLogger(YardiExample.class);

    public static void main(String[] args) {
        YardiClient c = new YardiClient();

        // Anya, use this code section to configure the parameters you would like to be sending
        YardiParameters yp = new YardiParameters();
        yp.setUsername(YardiConstants.USERNAME);
        yp.setPassword(YardiConstants.PASSWORD);
        yp.setServerName(YardiConstants.SERVER_NAME);
        yp.setDatabase(YardiConstants.DATABASE);
        yp.setPlatform(YardiConstants.PLATFORM);
        yp.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        yp.setYardiPropertyId(YardiConstants.YARDI_PROPERTY_ID);
//        yp.setYardiPropertyId("anya_2");

        // execute different actions
        try {
            // the order of this call should match the document order
            YardiTransactions.ping(c);
            YardiTransactions.getResidentTransactions(c, yp);

            // ANYA, use the first line if you want to send stuff, second to retrieve
            //send(c, yp);
            retrieve(c, yp);

            //YardiTransactions.getResidentTransactions(c, yp);
            //YardiTransactions.getResidentsLeaseCharges(c, yp);
        } catch (Throwable e) {
            log.error("error", e);
        }
    }

    private static void send(YardiClient c, YardiParameters yp) throws JAXBException, XMLStreamException, IOException {
        Charge charge = new Charge();
        Detail detail = new Detail();
        charge.setDetail(detail);

        // TODO - the code below does not do anything yet, just use the Charge.xml file
        detail.setBatchId("05/2010 Vista Charges");
        detail.setDescription("Application Fee");
        detail.setTransactionDate("2011-06-05");
        detail.setChargeCode("appfee");
        detail.setGlAccountNumber("58200000");
        detail.setCustomerId("t0000188");
        detail.setUnitId("104");
        detail.setAmountPaid("0");
        detail.setAmount("20.00");
        detail.setComment("Application Fee");
        detail.setPropertyPrimaryId(yp.getYardiPropertyId());

//        String xml = MarshallUtil.marshalls(charge);

        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("Payment.xml", XmlBeanTest.class));

        log.info("Sending\n{}\n", xml);
        yp.setTransactionXml(xml);

        YardiTransactions.importResidentTransactions(c, yp);

    }

    private static void retrieve(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException, JAXBException {
        YardiTransactions.getResidentTransactions(c, yp);
//    YardiTransactions.getResidentTransaction(c, yp);
        YardiTransactions.getResidentTransactionsByChargeDate(c, yp);
        YardiTransactions.getResidentTransactionsByApplicationDate(c, yp);
        YardiTransactions.getResidentsLeaseCharges(c, yp);
//    YardiTransactions.getResidentLeaseCharges(c, yp);
        YardiTransactions.getUnitInformationLogin(c, yp);
        YardiTransactions.getVendors(c, yp);
        YardiTransactions.getExportChartOfAccounts(c, yp);
        YardiTransactions.getPropertyConfigurations(c, yp);
    }
}
