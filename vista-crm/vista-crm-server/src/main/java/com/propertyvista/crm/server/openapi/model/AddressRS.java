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
package com.propertyvista.crm.server.openapi.model;

import javax.xml.bind.annotation.XmlElement;

public class AddressRS {

    @XmlElement(name = "streetNumber", required = true)
    public String streetNumber;

    @XmlElement(name = "streetName")
    public String streetName;

    @XmlElement(name = "streetType")
    public String streetType;

    @XmlElement(name = "city")
    public String city;

    @XmlElement(name = "province")
    public String province;

    @XmlElement(name = "postalCode")
    public String postalCode;

    @XmlElement(name = "country")
    public String country;
}
