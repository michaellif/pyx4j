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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RequestMessage {

    /**
     * Optional unique identifier for the XML message
     */
    @Size(max = 60)
    private String messageId;

    @Size(max = 60)
    @NotNull
    private String interfaceEntity;

    /**
     * You must provide your password for every HTTP request.
     */
    @Size(max = 60)
    @NotNull
    private String interfaceEntityPassword;

    /**
     * The MerchantId or Terminal ID is your "account number" at Caledon. This "account number" is 8 characters long, and is often alphanumeric (EBOOKSTR or
     * PIZZA001).
     */
    @Size(max = 8)
    @NotNull
    @Pattern(regexp = "[A-Za-z0-9]+")
    private String merchantId;

    /**
     * For added security, your "account number" at Caledon can be set up with a password. This password, if enabled, is required for all transactions to this
     * terminal ID.
     */
    @Size(max = 16)
    @Pattern(regexp = "[A-Za-z0-9]+")
    private String merchantPassword;

    private List<Request> requests;

    @XmlElement(required = false)
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @XmlElement(required = true)
    public String getInterfaceEntity() {
        return interfaceEntity;
    }

    public void setInterfaceEntity(String interfaceEntity) {
        this.interfaceEntity = interfaceEntity;
    }

    @XmlElement(required = true)
    public String getInterfaceEntityPassword() {
        return interfaceEntityPassword;
    }

    public void setInterfaceEntityPassword(String password) {
        this.interfaceEntityPassword = password;
    }

    @XmlElement(required = true)
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    @XmlElement(required = false)
    public String getMerchantPassword() {
        return merchantPassword;
    }

    public void setMerchantPassword(String merchantPassword) {
        this.merchantPassword = merchantPassword;
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
