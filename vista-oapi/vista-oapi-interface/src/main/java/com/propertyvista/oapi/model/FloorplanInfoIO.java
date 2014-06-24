/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 24, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.LogicalDateIO;
import com.propertyvista.oapi.xml.StringIO;

@XmlRootElement(name = "floorplanInfo")
public class FloorplanInfoIO extends AbstractElementIO {

    public StringIO marketingName;

    public StringIO description;

    public IntegerIO bedrooms;

    public IntegerIO dens;

    public IntegerIO bathrooms;

    public IntegerIO halfBath;

    public BigDecimalIO rentFrom;

    public IntegerIO sqftFrom;

    public LogicalDateIO availableFrom;
}
