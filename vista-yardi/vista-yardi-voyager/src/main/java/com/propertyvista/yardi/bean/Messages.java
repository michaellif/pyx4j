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
package com.propertyvista.yardi.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.propertyvista.yardi.bean.Message.MessageType;

@XmlRootElement(name = "Messages")
public class Messages {

    private List<Message> messages = new ArrayList<Message>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Message property : messages) {
            sb.append("\n").append(property);
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
        if (messages.size() != 1) {
            throw new Error("Can't parse Message");
        }
        if (messages.get(0).getType() == MessageType.FYI) {
            return false;
        } else {
            return true;
        }
    }
}
