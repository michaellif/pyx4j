/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 5, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.bean.out;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * <BatchID>02/2008 App. Charges ares319</BatchID>
 * <Description>Application Fee</Description>
 * <TransactionDate>2008-02-25</TransactionDate>
 * <ChargeCode>appfee</ChargeCode>
 * <GLAccountNumber>58200000</GLAccountNumber>
 * <CustomerID>t0000188</CustomerID>
 * <UnitID>104</UnitID>
 * <AmountPaid>0</AmountPaid>
 * <Amount>20.00</Amount>
 * <Comment>Application Fee</Comment>
 * <PropertyPrimaryID>ares319</PropertyPrimaryID>
 * 
 * @author dmitry
 * 
 */
@XmlRootElement
@XmlType(propOrder = { "batchId", "description", "transactionDate", "chargeCode", "glAccountNumber", "customerId", "unitId", "amountPaid", "amount", "comment",
        "propertyPrimaryId" })
public class Detail {

    private String batchId;

    private String description;

    private String transactionDate;

    private String chargeCode;

    private String glAccountNumber;

    private String customerId;

    private String unitId;

    private String amountPaid;

    private String amount;

    private String comment;

    private String propertyPrimaryId;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(batchId).append(" ").append(description).append("\n");
        sb.append(transactionDate).append(" ").append(chargeCode).append("\n");
        sb.append(glAccountNumber).append(" ").append(customerId).append("\n");
        sb.append(unitId).append(" ").append(amountPaid).append(" ").append(amount).append("\n");
        sb.append(comment).append("\n");
        sb.append(propertyPrimaryId);
        return sb.toString();
    }

    @XmlElement(name = "BatchID")
    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @XmlElement(name = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "TransactionDate")
    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
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

    @XmlElement(name = "AmountPaid")
    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    @XmlElement(name = "Amount")
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @XmlElement(name = "Comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @XmlElement(name = "PropertyPrimaryID")
    public String getPropertyPrimaryId() {
        return propertyPrimaryId;
    }

    public void setPropertyPrimaryId(String propertyPrimaryId) {
        this.propertyPrimaryId = propertyPrimaryId;
    }

}
