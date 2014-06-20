/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 13, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.service.marketing.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.pyx4j.commons.LogicalDate;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FloorplanAvailability {

    public String floorplanName;

    public BigDecimal marketRent;

    public Integer areaSqFeet;

    public LogicalDate dateAvailable;

}
