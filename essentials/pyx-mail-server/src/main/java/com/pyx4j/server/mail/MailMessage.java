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
 */
package com.pyx4j.server.mail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.lang3.SerializationUtils;

/**
 * N.B. Do not add rename members this objects is serialized in production database
 */
public class MailMessage implements Serializable {

    private static final long serialVersionUID = 3457527924951058240L;

    // Used to debug mailQueue problems
    private transient String mailQueueId;

    private String mailMessageObjectId;

    private String sender;

    private Collection<String> replyTo;

    private Collection<String> to;

    private Collection<String> cc;

    private Collection<String> bcc;

    private String subject;

    private String textBody;

    private String htmlBody;

    private List<MailAttachment> attachments;

    private Map<String, String> headers;

    private Collection<String> keywords;

    private Collection<Serializable> applicationAttributes;

    private transient String deliveryErrorMessage;

    public MailMessage() {
        mailMessageObjectId = UUID.randomUUID().toString();
    }

    public String getMailMessageObjectId() {
        return mailMessageObjectId;
    }

    private void setMailMessageObjectId() {
        mailMessageObjectId = UUID.randomUUID().toString();
    }

    public String getMailQueueId() {
        return mailQueueId;
    }

    public void setMailQueueId(String mailQueueId) {
        this.mailQueueId = mailQueueId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Collection<String> getReplyTo() {
        if (this.replyTo == null) {
            return Collections.emptyList();
        } else {
            return replyTo;
        }
    }

    public void addReplyTo(String replyTo) {
        if (this.replyTo == null) {
            this.replyTo = new ArrayList<>();
        }
        this.replyTo.add(replyTo);
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
        if (this.to == null) {
            return Collections.emptyList();
        } else {
            return to;
        }
    }

    public void setTo(Collection<String> to) {
        this.to = to;
    }

    public void setTo(String to) {
        if (to == null) {
            return;
        }
        if (this.to == null) {
            this.to = new ArrayList<>();
        }
        this.to.add(to);
    }

    public void addToList(String comaSeparatedAddresses) {
        if (this.to == null) {
            this.to = new ArrayList<>();
        }
        this.to.addAll(getAddressList(comaSeparatedAddresses));
    }

    public void addTo(Collection<String> addresses) {
        if (this.to == null) {
            this.to = new ArrayList<>();
        }
        this.to.addAll(addresses);
    }

    public Collection<String> getCc() {
        if (this.cc == null) {
            return Collections.emptyList();
        } else {
            return cc;
        }
    }

    public void setCc(Collection<String> cc) {
        this.cc = cc;
    }

    public void addCc(List<String> addresses) {
        if (this.cc == null) {
            this.cc = new ArrayList<>();
        }
        this.cc.addAll(addresses);
    }

    public void addCcList(String comaSeparatedAddresses) {
        if (this.cc == null) {
            this.cc = new ArrayList<>();
        }
        this.cc.addAll(getAddressList(comaSeparatedAddresses));
    }

    public Collection<String> getBcc() {
        if (this.bcc == null) {
            return Collections.emptyList();
        } else {
            return bcc;
        }
    }

    public void setBcc(Collection<String> bcc) {
        this.bcc = bcc;
    }

    public void addBccList(String comaSeparatedAddresses) {
        if (this.bcc == null) {
            this.bcc = new ArrayList<>();
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
            headers = new HashMap<>();
        }
        headers.put(name, value);
    }

    /**
     * Maps to RFC 2822 Keywords
     */
    public Collection<String> getKeywords() {
        if (this.keywords == null) {
            return Collections.emptyList();
        } else {
            return keywords;
        }
    }

    public void setKeywords(Collection<String> keywords) {
        this.keywords = keywords;
    }

    public void addKeywords(Collection<String> keywords) {
        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }
        this.keywords.addAll(keywords);
    }

    public void addKeywords(String... keyword) {
        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }
        this.keywords.addAll(Arrays.asList(keyword));
    }

    public void addApplicationAttribute(Serializable object) {
        if (applicationAttributes == null) {
            applicationAttributes = new ArrayList<>();
        }
        applicationAttributes.add(object);
    }

    public boolean containsApplicationAttribute(Serializable object) {
        if (applicationAttributes == null) {
            return false;
        } else {
            return applicationAttributes.contains(object);
        }
    }

    public Collection<Serializable> getApplicationAttributes() {
        if (applicationAttributes == null) {
            return Collections.emptyList();
        } else {
            return applicationAttributes;
        }
    }

    public List<MailAttachment> getAttachments() {
        return attachments;
    }

    public void addAttachment(MailAttachment attachment) {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        attachments.add(attachment);
    }

    public String getDeliveryErrorMessage() {
        return deliveryErrorMessage;
    }

    public void setDeliveryErrorMessage(String deliveryErrorMessage) {
        this.deliveryErrorMessage = deliveryErrorMessage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mailMessageObjectId == null) ? 0 : mailMessageObjectId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MailMessage other = (MailMessage) obj;
        if (mailMessageObjectId == null) {
            return false;
        } else {
            return mailMessageObjectId.equals(other.mailMessageObjectId);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("MailMessage: ");
        b.append("ObjectId   : ").append(mailMessageObjectId).append("\t");
        b.append("QueueId   : ").append(mailQueueId).append("\t");
        b.append("To   : ").append(to).append("\t");
        return b.toString();
    }

    public MailMessage duplicate() {
        MailMessage duplicateMsg = SerializationUtils.clone(this);
        duplicateMsg.setMailMessageObjectId();
        return duplicateMsg;
    }
}
