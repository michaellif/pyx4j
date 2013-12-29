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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "Message")
public class Message {

    public static enum MessageType {
        FYI, Error
    }

    public Message() {
    }

    public Message(MessageType type, String value) {
        this.type = type;
        this.value = value;
    }

    @XmlValue
    private String value;

    @XmlAttribute(name = "messageType")
    private MessageType type;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(value);

        return sb.toString();
    }

    public String getValue() {
        return value;
    }

    public MessageType getType() {
        return type;
    }

}
