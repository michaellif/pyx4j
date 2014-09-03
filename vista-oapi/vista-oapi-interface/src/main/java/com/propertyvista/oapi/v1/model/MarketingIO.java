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
package com.propertyvista.oapi.v1.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.StringIO;

@XmlType(name = "Marketing")
public class MarketingIO extends AbstractElementIO {

    @XmlAttribute
    public String name;

    public String newName;

    public StringIO description;

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "blurb", type = AdvertisingBlurbIO.class))
    public List<AdvertisingBlurbIO> blurbs = new ArrayList<AdvertisingBlurbIO>();

    @Override
    public boolean equals(Object obj) {
        return name == ((MarketingIO) obj).name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
