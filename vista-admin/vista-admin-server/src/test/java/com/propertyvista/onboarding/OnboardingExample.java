/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.xml.XMLEntitySchemaWriter;
import com.pyx4j.essentials.server.xml.XMLEntityWriter;
import com.pyx4j.essentials.server.xml.XMLStringWriter;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.interfaces.importer.xml.ImportXMLEntityName;
import com.propertyvista.onboarding.payment.CreditCardPaymentInstrumentIO;

public class OnboardingExample {

    private final static Logger log = LoggerFactory.getLogger(OnboardingExample.class);

    public static void main(String[] args) throws Exception {

        XMLEntitySchemaWriter.printSchema(RequestMessageIO.class, new FileOutputStream(new File("target", "onboardingRequest-model.xsd")), true);
        XMLEntitySchemaWriter.printSchema(ResponseMessageIO.class, new FileOutputStream(new File("target", "onboardingResponse-model.xsd")), true);

        {
            PaymentRequestIO pr = EntityFactory.create(PaymentRequestIO.class);
            pr.amount().setValue(new BigDecimal("25.00"));
            CreditCardPaymentInstrumentIO cc = EntityFactory.create(CreditCardPaymentInstrumentIO.class);
            pr.paymentInstrument().set(cc);
            cc.number().setValue("5191111111111111");
            cc.expiryDate().setValue(new LogicalDate(new SimpleDateFormat("yyyy-MM").parse("2014-12")));
            writeXML(createExampleRequest(pr), "PaymentRequest.xml");
        }

    }

    private static RequestMessageIO createExampleRequest(RequestIO request) {
        RequestMessageIO r = EntityFactory.create(RequestMessageIO.class);
        r.interfaceEntity().setValue("rossul");
        r.interfaceEntityPassword().setValue("secret");
        r.requests().add(request);
        return r;
    }

    private static void writeXML(IEntity io, String name) {
        File f = new File("target", name);
        FileWriter w = null;
        try {
            w = new FileWriter(f);
            XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"));
            XMLEntityWriter xmlWriter = new XMLEntityWriter(xml, new ImportXMLEntityName());
            xmlWriter.setEmitId(false);
            xmlWriter.write(io);
            w.write(xml.toString());
            w.flush();
        } catch (IOException e) {
            log.error("debug write", e);
        } finally {
            IOUtils.closeQuietly(w);
        }
    }

}
