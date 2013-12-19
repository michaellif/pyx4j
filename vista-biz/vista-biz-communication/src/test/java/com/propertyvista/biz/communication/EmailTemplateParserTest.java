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

import com.propertyvista.biz.communication.mail.template.EmailTemplateManager;

public class EmailTemplateParserTest extends TestCase {

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

    public void testMultipleLinks() {
        String protocol = "http://";
        String url = "aaa.bbb.ccc/ddd?eee";
        String body = "Click Here";

        String in = "[[" + protocol + url + "|" + body + "]] blah-blah blah [[" + protocol + url + "|" + body + "]]";
        String out = "<a href=\"" + protocol + url + "\">" + body + "</a> blah-blah blah <a href=\"" + protocol + url + "\">" + body + "</a>";

        assertEquals("Multiple Urls", out, EmailTemplateManager.parseTemplate(in, null));
    }

    // incomplete expression should not change the output
    public void testNegative() {
        String protocol = "http://";
        String url = "aaa.bbb.ccc/ddd?eee";
        String body = "Click Here";

        String in = "[[" + protocol + url + "|" + body;
        assertEquals("Negative Test", in, EmailTemplateManager.parseTemplate(in, null));

        in = "[[" + protocol + url + body;
        assertEquals("Negative Test", in, EmailTemplateManager.parseTemplate(in, null));

        in = protocol + url + "|" + body + "]]";
        assertEquals("Negative Test", in, EmailTemplateManager.parseTemplate(in, null));
    }
}
