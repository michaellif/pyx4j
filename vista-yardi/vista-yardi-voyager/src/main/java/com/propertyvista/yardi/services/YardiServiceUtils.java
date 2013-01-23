/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 23, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.yardi.bean.Message;
import com.propertyvista.yardi.bean.Messages;

public class YardiServiceUtils {

    private final static Logger log = LoggerFactory.getLogger(YardiServiceUtils.class);

    public static boolean isMessageResponse(String s) {
        return StringUtils.startsWith(s, "<Messages>") && StringUtils.endsWith(s, "</Messages>");
    }

    public static String toString(Messages messages) {

        StringBuilder sb = new StringBuilder();
        sb.append("Unexpected response received: \n");

        for (Message message : messages.getMessages()) {
            if (StringUtils.isNotEmpty(message.getValue())) {
                sb.append(String.format("Message type= %s, value= %s", message.getType(), message.getValue())).append("\n");
            }
        }

        return sb.toString();
    }

}
