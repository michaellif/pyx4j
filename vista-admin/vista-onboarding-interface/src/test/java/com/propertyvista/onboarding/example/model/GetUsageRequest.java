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

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import com.propertyvista.onboarding.UsageType;

public class GetUsageRequest extends Request {
    public enum UsageReportFormatType {

        /**
         * Returns result as one Record with totals for given period
         */
        Short,

        /**
         * Returns every usage record for given period
         */
        Detailed

    }

    @NotNull
    @XmlElement
    public UsageType usageType;

    public Date from;

    public Date to;

    @NotNull
    @XmlElement
    public UsageReportFormatType format;
}
