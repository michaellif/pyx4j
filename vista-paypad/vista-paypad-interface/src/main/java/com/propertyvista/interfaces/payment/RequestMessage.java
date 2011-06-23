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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RequestMessage {

    private String messageID;

    private String interfaceEntity;

    /**
     * You must provide your password for every HTTP request.
     */
    private String password;

    private String merchantId;

    private List<Request> requests;

    @XmlElement(required = false)
    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    @XmlElement(required = true)
    public String getInterfaceEntity() {
        return interfaceEntity;
    }

    public void setInterfaceEntity(String interfaceEntity) {
        this.interfaceEntity = interfaceEntity;
    }

    @XmlElement(required = true)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlElement(required = true)
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    //@formatter:off
    @XmlElementWrapper
    @XmlElements({ 
        @XmlElement(name = "transaction", type = TransactionRequest.class), 
        @XmlElement(name = "tokenAction", type = TokenActionRequest.class) })
    //@formatter:on    
    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public void addRequest(Request request) {
        if (requests == null) {
            requests = new ArrayList<Request>();
        }
        this.requests.add(request);
    }

}
