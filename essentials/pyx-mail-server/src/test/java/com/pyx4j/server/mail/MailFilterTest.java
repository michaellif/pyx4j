/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2013-06-19
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.server.mail;

import static com.pyx4j.server.mail.SMTPMailUtils.emails;
import static com.pyx4j.server.mail.SMTPMailUtils.filterDestinations;

import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import junit.framework.Assert;

import org.junit.Test;

public class MailFilterTest {

    @Test
    public void testFilterDestinations() throws AddressException {
        MailMessage m = new MailMessage();
        m.addToList("bob@gmail.com,dan@hotmail.com,vlad@pyx4j.com");
        List<InternetAddress> address1 = filterDestinations("pyx4j.com", emails(m.getTo()));
        Assert.assertEquals("only one selected", 1, address1.size());

        List<InternetAddress> address2 = filterDestinations("pyx4j.com;hotmail.com", emails(m.getTo()));
        Assert.assertEquals("two selected", 2, address2.size());

        address2 = filterDestinations("pyx4j.com;gmail.com", emails(m.getTo()));
        Assert.assertEquals("two selected", 2, address2.size());
    }
}
