/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-02-20
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.server.mail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

public class MailMessage implements Serializable {

    private static final long serialVersionUID = -104910075896758956L;

    private String sender;

    private String replyTo;

    private Collection<String> to;

    private Collection<String> cc;

    private Collection<String> bcc;

    private String subject;

    private String textBody;

    private String htmlBody;

    private List<MailAttachment> attachments;

    private Map<String, String> headers;

    public MailMessage() {

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public static List<String> getAddressList(String comaSeparatedAddresses) {
        comaSeparatedAddresses = comaSeparatedAddresses.replaceAll(";", ",");
        List<String> recipients = new Vector<String>();
        StringTokenizer st = new StringTokenizer(comaSeparatedAddresses, ",");
        if (!st.hasMoreTokens()) {
            recipients.add(comaSeparatedAddresses.trim());
        } else {
            while (st.hasMoreTokens()) {
                String email = st.nextToken().trim();
                if (email.length() > 0) {
                    recipients.add(email);
                }
            }
        }
        return recipients;
    }

    public Collection<String> getTo() {
        return to;
    }

    public void setTo(Collection<String> to) {
        this.to = to;
    }

    public void setTo(String to) {
        if (this.to == null) {
            this.to = new Vector<String>();
        }
        this.to.add(to);
    }

    public void addToList(String comaSeparatedAddresses) {
        if (this.to == null) {
            this.to = new Vector<String>();
        }
        this.to.addAll(getAddressList(comaSeparatedAddresses));
    }

    public Collection<String> getCc() {
        return cc;
    }

    public void setCc(Collection<String> cc) {
        this.cc = cc;
    }

    public void addCcList(String comaSeparatedAddresses) {
        if (this.cc == null) {
            this.cc = new Vector<String>();
        }
        this.cc.addAll(getAddressList(comaSeparatedAddresses));
    }

    public Collection<String> getBcc() {
        return bcc;
    }

    public void setBcc(Collection<String> bcc) {
        this.bcc = bcc;
    }

    public void addBccList(String comaSeparatedAddresses) {
        if (this.bcc == null) {
            this.bcc = new Vector<String>();
        }
        this.bcc.addAll(getAddressList(comaSeparatedAddresses));
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    public Set<Map.Entry<String, String>> getHeaders() {
        if (headers == null) {
            return Collections.emptySet();
        }
        return headers.entrySet();
    }

    public String getHeader(String name) {
        return headers == null || name == null ? null : headers.get(name);
    }

    public void setHeader(String name, String value) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(name, value);
    }

    public List<MailAttachment> getAttachments() {
        return attachments;
    }

    public void addAttachment(MailAttachment attachment) {
        if (attachments == null) {
            attachments = new ArrayList<MailAttachment>();
        }
        attachments.add(attachment);
    }
}
