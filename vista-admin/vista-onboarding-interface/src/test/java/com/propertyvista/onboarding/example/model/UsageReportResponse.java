/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 30, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.onboarding.example.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

import com.propertyvista.onboarding.GetUsageRequestIO.UsageReportFormatType;

public class UsageReportResponse extends Response {

    @XmlElement
    public UsageReportFormatType format;

    @XmlElementWrapper
    @XmlElements({ @XmlElement(name = "usageRecord", type = UsageRecord.class) })
    public List<UsageRecord> records;
}
