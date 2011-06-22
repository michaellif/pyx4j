/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.payment;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RequestMessage {

    @XmlElement(required = false)
    public String messageID;

    @XmlElement(required = true)
    public String interfaceEntity;

    /**
     * You must provide your password for every HTTP request.
     */
    @XmlElement(required = true)
    public String password;

    @XmlElement(required = true)
    public String merchantId;

    //@formatter:off
    @XmlElementWrapper
    @XmlElements({ 
        @XmlElement(name = "transaction", type = TransactionRequest.class), 
        @XmlElement(name = "tokenAction", type = TokenActionRequest.class),
        @XmlElement(name = "canadianAVS", type = CanadianAVSRequest.class), })
    public List<Request> requests = new Vector<Request>();

}
