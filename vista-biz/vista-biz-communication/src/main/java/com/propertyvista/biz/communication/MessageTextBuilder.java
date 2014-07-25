/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 25, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.MessageDTO;

public class MessageTextBuilder {

    public static String buildForwardSubject(MessageDTO forwardedMessage) {
        if (forwardedMessage == null || forwardedMessage.thread() == null || forwardedMessage.thread().subject() == null) {
            return null;
        }

        String result = forwardedMessage.thread().subject().getValue() == null ? "" : forwardedMessage.thread().subject().getValue();
        if (result.startsWith("Fwd: ")) {
            return result;
        }

        if (result.startsWith("Re: ")) {
            result = result.substring(3);
        }

        return "Fwd: " + result;
    }

    public static String buildForwardText(MessageDTO forwardedMessage) {
        if (forwardedMessage == null) {
            return null;
        }
        StringBuffer bodyText = new StringBuffer();
        StringBuffer buffer = null;
        new StringBuffer();
        for (CommunicationEndpointDTO recipient : forwardedMessage.to()) {
            if (buffer == null) {
                buffer = new StringBuffer();
            } else {
                buffer.append(", ");
            }
            buffer.append(recipient.name().getValue());
        }

        bodyText.append("\n---------- Forwarded message ----------");
        bodyText.append("\nFrom: ");
        bodyText.append(forwardedMessage.sender().name().getValue());
        bodyText.append("\nDate: ");
        bodyText.append(forwardedMessage.date().getStringView());
        bodyText.append("\nSubject: ");
        bodyText.append(forwardedMessage.subject().getValue());
        bodyText.append("\nTo: ");
        bodyText.append(buffer.toString());
        bodyText.append("\n\nFwd:\n");
        bodyText.append(forwardedMessage.text().getValue());

        return bodyText.toString();
    }
}
