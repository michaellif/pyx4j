/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 20, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.ils.communication;

import junit.framework.Assert;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.SMTPGmailMailServiceConfig;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class SendMailTest extends VistaDBTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Mail.getMailService().setDisabled(false);
    }

    public void testScenario() {
        try {
            MailSendFacade mailFacade = new MailSendFacade();
            testSend(mailFacade);
            testFailedSend(mailFacade);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testFailedSend(MailSendFacade mailFacade) {
        Exception ex = null;
        try {
            mailFacade.sendMail(createInput(false));
            fail("Should throw invalid argument exception on empty input");
        } catch (Exception ee) {
            ex = ee;
        }
        Assert.assertTrue(ex instanceof IllegalArgumentException);
    }

    private void testSend(MailSendFacade mailFacade) {
        Assert.assertEquals(MailDeliveryStatus.Success, mailFacade.sendMail(createInput(true)));
    }

    private FeedEMail createInput(final boolean returnTo) {
        return new FeedEMail() {

            @Override
            public boolean isHtmlbody() {
                return true;
            }

            @Override
            public String getTo() {

                return returnTo ? "testtestovichsmtp@gmail.com" : null;
            }

            @Override
            public String getSubject() {
                return "Test Ils";
            }

            @Override
            public String getSender() {
                return "testtestovichsmtp@gmail.com";
            }

            @Override
            public IMailServiceConfigConfiguration getMailServiceConfiguration() {
                Credentials c = new Credentials();
                c.userName = "testtestovichsmtp";
                c.password = "testsmtp";
                return new SMTPGmailMailServiceConfig(c);
            }

            @Override
            public String getCc() {
                return null;
            }

            @Override
            public String getBody() {
                return "<html>Nothing special. Test only.</html>";
            }

            @Override
            public AttachmentDescriptor getAttachment() {
                return new AttachmentDescriptor() {

                    @Override
                    public String getName() {
                        return "feed.xml";
                    }

                    @Override
                    public String getContentType() {
                        return "text/xml";
                    }

                    @Override
                    public String getAttachment() {
                        return "<xml><test/></xml>";
                    }
                };
            }
        };
    }

}
