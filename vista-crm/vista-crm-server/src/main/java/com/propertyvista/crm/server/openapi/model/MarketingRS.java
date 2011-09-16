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
 * @author dmitry
 */
package com.propertyvista.crm.server.openapi.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

public class MarketingRS {

    public String name;

    public String description;

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "blurb", type = AdvertisingBlurbRS.class))
    public List<AdvertisingBlurbRS> blurbs = new ArrayList<AdvertisingBlurbRS>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "phone", type = PhoneRS.class))
    public List<PhoneRS> phones = new ArrayList<PhoneRS>();

}
