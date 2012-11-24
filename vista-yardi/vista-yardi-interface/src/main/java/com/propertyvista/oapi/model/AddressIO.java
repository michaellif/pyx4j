/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 27, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.model;

import javax.xml.bind.annotation.XmlElement;

import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.StringIO;

public class AddressIO extends AbstractElementIO {

    public StringIO addressType;

    //mandatory for portal
    @XmlElement(nillable = true)
    public StringIO streetNumber;

    //mandatory for portal
    public StringIO streetName;

    public StringIO streetNumberSuffix;

    public StringIO streetDirection;

    //mandatory for portal
    public StringIO streetType;

    public StringIO unitNumber;

    //mandatory for portal
    public StringIO city;

    //mandatory for portal
    public StringIO province;

    public StringIO provinceCode;

    public StringIO postalCode;

    //mandatory for portal
    public StringIO country;

    public StringIO county;

    //mandatory for portal
    public GeoLocationIO location;
}
