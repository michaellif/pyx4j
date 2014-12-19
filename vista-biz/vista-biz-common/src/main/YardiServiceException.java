/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 28, 2012
 * @author Mykola
 */
package com.propertyvista.yardi;

import com.propertyvista.yardi.bean.Message.MessageType;

/**
 * Signals about service operation failure during YARDI System api calls
 * 
 * @author Mykola
 * 
 */
@SuppressWarnings("serial")
public class YardiServiceException extends Exception {

    private MessageType messageType;

    public YardiServiceException() {
        super();
    }

    public YardiServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public YardiServiceException(String message) {
        super(message);
    }

    public YardiServiceException(MessageType messageType, String message) {
        super(message);
        this.messageType = messageType;
    }

    public YardiServiceException(Throwable cause) {
        super(cause);
    }

    public MessageType getMessageType() {
        return messageType;
    }

}
