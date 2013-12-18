/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.biz.communication.mail.template.EmailTemplateManager;

public class EmailTemplateParserTest extends TestCase {

    private final static Logger log = LoggerFactory.getLogger(EmailTemplateParserTest.class);

    public void testLinkParserBoth() {
        String protocol = "http://";
        String url = "aaa.bbb.ccc/ddd?eee";
        String body = "Click Here";

        String in = "[[" + protocol + url + "|" + body + "]]";
        String out = "<a href=\"" + protocol + url + "\">" + body + "</a>";

        assertEquals("Url + Body", out, EmailTemplateManager.parseTemplate(in, null));
    }

    public void testLinkParserUrlOnly() {
        String protocol = "http://";
        String url = "aaa.bbb.ccc/ddd?eee";
        String body = "";

        String in = "[[" + protocol + url + "|" + body + "]]";
        String out = "<a href=\"" + protocol + url + "\">" + url + "</a>";

        assertEquals("Url only", out, EmailTemplateManager.parseTemplate(in, null));
    }
}
