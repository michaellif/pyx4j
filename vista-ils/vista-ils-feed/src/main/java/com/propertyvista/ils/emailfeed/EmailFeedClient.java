/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils.emailfeed;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kijiji.pint.rs.ILSLocations;
import com.kijiji.pint.rs.ObjectFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailAttachment;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.occupancy.ILSEmailFeedIntegrationAgent;
import com.propertyvista.domain.settings.ILSEmailConfig;
import com.propertyvista.ils.kijiji.mapper.KijijiDataMapper;

/**
 * @author smolka
 *         The class responsible to import data to the gottarent server
 */
public class EmailFeedClient {

    private final static Logger log = LoggerFactory.getLogger(EmailFeedClient.class);

    public static void emailFeed(ExecutionMonitor executionMonitor) {
        // TODO - use ExecutionMonitor to register state (target points, errors, etc) in the course of execution
        try {
            EntityQueryCriteria<ILSEmailConfig> critIls = EntityQueryCriteria.create(ILSEmailConfig.class);
            List<ILSEmailConfig> ilsCfgs = null;
            try {
                ilsCfgs = Persistence.service().query(critIls);

            } catch (Exception ignore) {
                // noop
            }

            if (ilsCfgs == null || ilsCfgs.size() < 1) {
                return;
            }

            for (ILSEmailConfig ilsEmailCfg : ilsCfgs) {
                // currently based on kijijji implementation
                com.kijiji.pint.rs.ObjectFactory factory = new com.kijiji.pint.rs.ObjectFactory();
                ILSLocations locations = new KijijiDataMapper(factory).createLocations(new ILSEmailFeedIntegrationAgent(ilsEmailCfg).getUnitListing());
                EmailFeedClient.sendEmailFeed(locations, factory, ilsEmailCfg);
            }

        } catch (Exception e) {// TODO: Smolka
            throw new RuntimeException(e);
        }

    }

    private static MailDeliveryStatus sendEmailFeed(ILSLocations locations, ObjectFactory factory, ILSEmailConfig ilsCfg) throws Exception {
        if (locations == null || locations.getLocation().size() < 1 || ilsCfg == null || ilsCfg.email() == null || ilsCfg.email().isNull()) {
            //TODO: Smolka
            return MailDeliveryStatus.Success;
        }
        String generatedXml = generateXml(locations, factory);

        DataDump.dump("EmailFeed", generatedXml);
        return sendEmail(ilsCfg.email().getValue(), generatedXml);

    }

    private static MailDeliveryStatus sendEmail(String emailAddress, String generatedXml) {
        MailMessage mailMessage = new MailMessage();

        mailMessage.setTo(emailAddress);
        mailMessage.setSender(ServerSideConfiguration.instance().getApplicationEmailSender());
        mailMessage.setSubject("Email Feed");
        // TODO: provide body & additional properties
        mailMessage.setHtmlBody("<html>Automatic email feed notification.</html>");

        mailMessage.addAttachment(new MailAttachment("Feed.xml", "text/xml", generatedXml.getBytes()));

        return Mail.send(mailMessage);
    }

    private static String generateXml(ILSLocations locations, ObjectFactory factory) throws JAXBException, PropertyException {
        StringWriter stringWriter = new StringWriter();
        Result res = new StreamResult(stringWriter);
        JAXBContext context = JAXBContext.newInstance(ILSLocations.class);
        javax.xml.bind.Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        JAXBElement<ILSLocations> element = factory.createLocations(locations);

        marshaller.marshal(element, res);
        return stringWriter.getBuffer().toString();
    }

}
