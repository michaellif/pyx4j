/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 27, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.propertyvista.yardi.beans.Message.MessageType;

@XmlRootElement(name = "Messages")
public class Messages {

    private List<Message> messages = new ArrayList<Message>();

    public static boolean isMessageResponse(String s) {
        return StringUtils.startsWith(s, "<Messages>") && StringUtils.endsWith(s, "</Messages>");
    }

    public static Messages create(Message... messages) {
        Messages msgs = new Messages();
        msgs.getMessages().addAll(Arrays.asList(messages));
        return msgs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Message message : messages) {
            if (StringUtils.isNotEmpty(message.getValue())) {
                sb.append(String.format("%s %s", message.getType(), message.getValue())).append("\n");
            }
        }

        return sb.toString();
    }

    @XmlElement(name = "Message")
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public boolean isError() {
        if (messages.size() == 0) {
            throw new Error("Can't parse Message");
        }

        for (Message message : messages) {
            if (message.getType() == MessageType.Error) {
                return true;
            }
        }
        return false;
    }

    public Message getErrorMessage() {
        if (messages.size() == 0) {
            throw new Error("Can't parse Message");
        }

        for (Message message : messages) {
            if (message.getType() == MessageType.Error) {
                return message;
            }
        }
        return null;
    }

    public boolean hasErrorMessage(String... messageFragments) {
        for (Message message : messages) {
            if ((message.getType() == MessageType.Error) && (message.getValue() != null)) {
                for (String messageFragment : messageFragments) {
                    if (message.getValue().contains(messageFragment)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getPrettyErrorMessageText() {
        if (messages.size() == 0) {
            throw new Error("Can't parse Message");
        }
        for (Message message : messages) {
            if (message.getType() == MessageType.Error) {
                String text = message.getValue();

                text = text.replace("Message Type=Error.", "");
                text = text.replace("Item Number=0.", "");

                return text.trim();
            }
        }
        return null;
    }

}
