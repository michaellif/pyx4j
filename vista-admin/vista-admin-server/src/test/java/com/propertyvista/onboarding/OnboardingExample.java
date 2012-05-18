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

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.xml.XMLEntityModelWriter;
import com.pyx4j.essentials.server.xml.XMLEntitySchemaWriter;
import com.pyx4j.essentials.server.xml.XMLEntityWriter;
import com.pyx4j.essentials.server.xml.XMLStringWriter;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.interfaces.importer.xml.ImportXMLEntityNamingConvention;
import com.propertyvista.onboarding.GetUsageRequestIO.UsageReportFormatType;
import com.propertyvista.onboarding.ProvisionPMCRequestIO.VistaFeature;
import com.propertyvista.onboarding.ProvisionPMCRequestIO.VistaLicense;
import com.propertyvista.onboarding.payment.CreditCardPaymentInstrumentIO;
import com.propertyvista.onboarding.payment.PaymentRequestIO;

public class OnboardingExample {

    private final static Logger log = LoggerFactory.getLogger(OnboardingExample.class);

    public static void main(String[] args) throws Exception {
        System.setProperty("com.pyx4j.EclipseDeveloperEnviroment", "true");

        XMLEntitySchemaWriter.printSchema(new FileOutputStream(new File("target", "onboardingRequest-model.xsd")), true, RequestMessageIO.class);
        XMLEntitySchemaWriter.printSchema(new FileOutputStream(new File("target", "onboardingResponse-model.xsd")), true, ResponseMessageIO.class);
        XMLEntitySchemaWriter.printSchema(new FileOutputStream(new File("target", "onboarding.xsd")), true, RequestMessageIO.class, ResponseMessageIO.class);

        if (false) {
            writeModelXML(EntityFactory.create(RequestMessageIO.class), "requests-all-example.xml");
            writeModelXML(EntityFactory.create(ResponseMessageIO.class), "response-all-example.xml");
        }

        int cnt = 0;
        {
            cnt++;
            CheckAvailabilityRequestIO r = EntityFactory.create(CheckAvailabilityRequestIO.class);
            r.dnsName().setValue("star11");
            RequestMessageIO rm = createExampleRequest(r);
            writeXML(rm, cnt + "-request-Check.xml");
            writeXML(createExampleResponse(), cnt + "-response-Check.xml");
        }

        {
            cnt++;
            UpdateAccountInfoRequestIO r = EntityFactory.create(UpdateAccountInfoRequestIO.class);
            r.onboardingAccountId().setValue("star");
            createAccountInfoIO(r.accountInfo());

            writeXML(createExampleRequest(r), cnt + "-request-UpdateAccountInfo.xml");
            AccountInfoResponseIO rs = EntityFactory.create(AccountInfoResponseIO.class);
            rs.success().setValue(Boolean.TRUE);
            createAccountInfoIO(rs.accountInfo());
            writeXML(createExampleResponse(rs), cnt + "-response-UpdateAccountInfo.xml");
        }

        {
            cnt++;
            CreatePMCRequestIO r = EntityFactory.create(CreatePMCRequestIO.class);
            r.onboardingAccountId().setValue("star");
            r.name().setValue("Star Starlight");
            r.dnsNameAliases().add("www.rentstarlight.com");
            r.dnsNameAliases().add("www.rentstarlight.ca");
            writeXML(createExampleRequest(r), cnt + "-request-Create.xml");
            writeXML(createExampleResponse(), cnt + "-response-Create.xml");
        }

        {
            cnt++;
            ActivatePMCRequestIO r = EntityFactory.create(ActivatePMCRequestIO.class);
            r.onboardingAccountId().setValue("star");
            r.country().setValue("Canada");
            r.license().setValue(VistaLicense.Unlimited);
            r.feature().setValue(VistaFeature.tbd1);
            writeXML(createExampleRequest(r), cnt + "-request-Activate.xml");
            writeXML(createExampleResponse(), cnt + "-response-Activate.xml");
        }

        {
            cnt++;
            GetAccountInfoRequestIO r = EntityFactory.create(GetAccountInfoRequestIO.class);
            r.onboardingAccountId().setValue("star");
            writeXML(createExampleRequest(r), cnt + "-request-GetAccountInfo.xml");
            AccountInfoResponseIO rs = EntityFactory.create(AccountInfoResponseIO.class);
            createAccountInfoIO(rs.accountInfo());
            rs.success().setValue(Boolean.TRUE);
            writeXML(createExampleResponse(rs), cnt + "-response-GetAccountInfo.xml");
        }

        {
            cnt++;
            GetUsageRequestIO r = EntityFactory.create(GetUsageRequestIO.class);
            r.onboardingAccountId().setValue("star");
            r.format().setValue(UsageReportFormatType.Short);
            r.from().setValue(DateUtils.detectDateformat("2011-01-01"));
            r.to().setValue(DateUtils.detectDateformat("2011-02-01"));
            writeXML(createExampleRequest(r), cnt + "-request-GetUsage.xml");

            UsageReportResponseIO rs = EntityFactory.create(UsageReportResponseIO.class);
            rs.success().setValue(Boolean.TRUE);
            rs.format().setValue(UsageReportFormatType.Short);
            UsageRecordIO u = EntityFactory.create(UsageRecordIO.class);
            rs.records().add(u);
            u.from().setValue(DateUtils.detectDateformat("2011-01-01"));
            u.to().setValue(DateUtils.detectDateformat("2011-02-01"));
            u.usageType().setValue(UsageType.Equifax);
            u.value().setValue(21);

            writeXML(createExampleResponse(rs), cnt + "-response-GetUsage.xml");
        }

        {
            cnt++;
            PaymentRequestIO pr = EntityFactory.create(PaymentRequestIO.class);
            pr.onboardingAccountId().setValue("star");
            pr.amount().setValue(new BigDecimal("25.00"));
            CreditCardPaymentInstrumentIO cc = EntityFactory.create(CreditCardPaymentInstrumentIO.class);
            pr.paymentInstrument().set(cc);
            cc.number().setValue("5191111111111111");
            cc.expiryDate().setValue(new LogicalDate(new SimpleDateFormat("yyyy-MM").parse("2014-12")));
            writeXML(createExampleRequest(pr), cnt + "-request-payment.xml");
            writeXML(createExampleResponse(), cnt + "-response-payment.xml");
        }

    }

    private static RequestMessageIO createExampleRequest(RequestIO request) {
        RequestMessageIO r = EntityFactory.create(RequestMessageIO.class);
        r.interfaceEntity().setValue("rossul");
        r.interfaceEntityPassword().setValue("secret");
        r.requests().add(request);
        return r;
    }

    private static void createAccountInfoIO(AccountInfoIO accountInfo) {
        accountInfo.firstName().setValue("Bob");
        accountInfo.lastName().setValue("Doe");
        accountInfo.phone().setValue("416-994-4590");
        accountInfo.address().streetNumber().setValue("10");
        accountInfo.address().streetName().setValue("Lake");
        accountInfo.address().streetType().setValue(StreetTypeIO.square);
        accountInfo.address().city().setValue("Toronto");
        accountInfo.address().countryName().setValue("Canada");
        accountInfo.address().postalCode().setValue("ON");
    }

    private static ResponseMessageIO createExampleResponse() {
        ResponseIO response = EntityFactory.create(ResponseIO.class);
        response.success().setValue(Boolean.TRUE);
        return createExampleResponse(response);
    }

    private static ResponseMessageIO createExampleResponse(ResponseIO response) {
        ResponseMessageIO r = EntityFactory.create(ResponseMessageIO.class);
        r.status().setValue(ResponseMessageIO.StatusCode.OK);
        r.responses().add(response);
        return r;
    }

    private static void writeXML(IEntity io, String name) {
        File f = new File("target", name);
        FileWriter w = null;
        try {
            w = new FileWriter(f);
            String namespace = null;
            javax.xml.bind.annotation.XmlSchema schema = io.getValueClass().getPackage().getAnnotation(javax.xml.bind.annotation.XmlSchema.class);
            if ((schema != null) && (CommonsStringUtils.isStringSet(schema.namespace()))) {
                namespace = schema.namespace();
            }
            XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"), namespace);
            xml.setSchemaLocation("http://interfaces.birchwoodsoftwaregroup.com/schema/onboarding");
            XMLEntityWriter xmlWriter = new XMLEntityWriter(xml, new ImportXMLEntityNamingConvention());
            xmlWriter.setEmitId(false);
            xmlWriter.write(io);
            w.write(xml.toString());
            w.flush();
        } catch (IOException e) {
            log.error("debug write", e);
        } finally {
            IOUtils.closeQuietly(w);
        }
        validate(f);
    }

    private static void writeModelXML(IEntity io, String name) {
        File f = new File("target", name);
        FileWriter w = null;
        try {
            w = new FileWriter(f);
            XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"));
            XMLEntityModelWriter xmlWriter = new XMLEntityModelWriter(xml, new ImportXMLEntityNamingConvention());
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

    private static void validate(File f) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            File schemaLocation = new File("target", "onboarding.xsd");
            Schema schema = factory.newSchema(schemaLocation);
            Validator validator = schema.newValidator();

            Source source = new StreamSource(f);
            validator.validate(source);
            log.info(f.getName() + " is valid.");
        } catch (SAXException ex) {
            log.error(f.getName() + " is not valid {}", ex.getMessage());
        } catch (IOException e) {
            log.error("Error", e);
        }
    }

}
