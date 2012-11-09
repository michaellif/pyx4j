/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 26, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.bean.mits;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

import com.propertyvista.yardi.mapper.YardiXmlUtil;

/**
 * <MITS:Lease>
 * <MITS:CurrentRent>850.00</MITS:CurrentRent>
 * <MITS:ExpectedMoveInDate>2011-06-24</MITS:ExpectedMoveInDate>
 * <MITS:LeaseFromDate>2011-06-24</MITS:LeaseFromDate>
 * <MITS:LeaseToDate>2012-06-30</MITS:LeaseToDate>
 * <MITS:ResponsibleForLease>true</MITS:ResponsibleForLease>
 * </MITS:Lease>
 */
public class Lease {

    private Double currentRent;

    private Date expectedMoveInDate;

    private Date leaseFromDate;

    private Date leaseToDate;

    private Boolean responsibleForLease;

    private Date actualMoveIn;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Lease:\n$");
        sb.append(currentRent);
        if (expectedMoveInDate != null) {
            sb.append(" expectedMoveIn=").append(YardiXmlUtil.strDate(expectedMoveInDate));
        }
        if (actualMoveIn != null) {
            sb.append(" actualMoveIn=").append(YardiXmlUtil.strDate(actualMoveIn));
        }
        if (leaseFromDate != null && leaseToDate != null) {
            sb.append(" Terms: ").append(YardiXmlUtil.strDate(leaseFromDate)).append("-");
            sb.append(YardiXmlUtil.strDate(leaseToDate)).append(" ");
        }
        sb.append(responsibleForLease);

        return sb.toString();
    }

    @XmlElement(name = "CurrentRent")
    public Double getCurrentRent() {
        return currentRent;
    }

    public void setCurrentRent(Double currentRent) {
        this.currentRent = currentRent;
    }

    @XmlElement(name = "ExpectedMoveInDate")
    public Date getExpectedMoveInDate() {
        return expectedMoveInDate;
    }

    public void setExpectedMoveInDate(Date expectedMoveInDate) {
        this.expectedMoveInDate = expectedMoveInDate;
    }

    @XmlElement(name = "LeaseFromDate")
    public Date getLeaseFromDate() {
        return leaseFromDate;
    }

    public void setLeaseFromDate(Date leaseFromDate) {
        this.leaseFromDate = leaseFromDate;
    }

    @XmlElement(name = "LeaseToDate")
    public Date getLeaseToDate() {
        return leaseToDate;
    }

    public void setLeaseToDate(Date leaseToDate) {
        this.leaseToDate = leaseToDate;
    }

    @XmlElement(name = "ResponsibleForLease")
    public Boolean getResponsibleForLease() {
        return responsibleForLease;
    }

    public void setResponsibleForLease(Boolean responsibleForLease) {
        this.responsibleForLease = responsibleForLease;
    }

    @XmlElement(name = "ActualMoveIn")
    public Date getActualMoveIn() {
        return actualMoveIn;
    }

    public void setActualMoveIn(Date actualMoveIn) {
        this.actualMoveIn = actualMoveIn;
    }
}
