/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.bean.resident;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

import com.propertyvista.yardi.mapper.YardiXmlUtil;

/**
 * <Detail>
 * <Description>Electric</Description>
 * <Service Type="Electric" />
 * <ChargeCode>elect</ChargeCode>
 * <GLAccountNumber>54700000</GLAccountNumber>
 * <CustomerID>t0005518</CustomerID>
 * <UnitID>141</UnitID>
 * </Detail>
 * 
 * <Detail>
 * <Description>Administration</Description>
 * <TransactionDate>2011-05-24</TransactionDate>
 * <TransactionID>700031079</TransactionID>
 * <ChargeCode>admin</ChargeCode>
 * <GLAccountNumber>53000000</GLAccountNumber>
 * <CustomerID>t0005518</CustomerID>
 * <UnitID>141</UnitID>
 * <AmountPaid>0</AmountPaid>
 * <BalanceDue>25.00</BalanceDue>
 * <Amount>25.00</Amount>
 * <Comment>Application Fee</Comment>
 * </Detail>
 */
public class Detail {
    private String description;

    private Date transactionDate;

    private String transactionId;

    private Service service;

    private String chargeCode;

    private String glAccountNumber;

    private String customerId;

    private String unitId;

    private Double amountPaid;

    private Double balanceDue;

    private Double amount;

    private String comment;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(description).append(" ");
        if (service != null) {
            sb.append("(").append(service.getType()).append(") ");
        }
        sb.append(chargeCode).append(" GL: ").append(glAccountNumber);
        if (transactionId != null) {
            sb.append(" TransactionId=").append(transactionId);
            sb.append(" ").append(YardiXmlUtil.strDate(transactionDate));

            sb.append(" Paid $").append(amountPaid).append(" $").append(amount);
            sb.append(" Balance $").append(balanceDue);
            sb.append(" (").append(comment).append(")");
        }

        return sb.toString();
    }

    @XmlElement(name = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "Service")
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @XmlElement(name = "ChargeCode")
    public String getChargeCode() {
        return chargeCode;
    }

    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    @XmlElement(name = "GLAccountNumber")
    public String getGlAccountNumber() {
        return glAccountNumber;
    }

    public void setGlAccountNumber(String glAccountNumber) {
        this.glAccountNumber = glAccountNumber;
    }

    @XmlElement(name = "CustomerID")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @XmlElement(name = "UnitID")
    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    @XmlElement(name = "TransactionDate")
    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    @XmlElement(name = "TransactionID")
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @XmlElement(name = "AmountPaid")
    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    @XmlElement(name = "BalanceDue")
    public Double getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(Double balanceDue) {
        this.balanceDue = balanceDue;
    }

    @XmlElement(name = "Amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @XmlElement(name = "Comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
