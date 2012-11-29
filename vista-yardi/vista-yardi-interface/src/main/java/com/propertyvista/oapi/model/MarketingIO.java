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
package com.propertyvista.oapi.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlValue;

import com.propertyvista.oapi.xml.Action;
import com.propertyvista.oapi.xml.ElementIO;
import com.propertyvista.oapi.xml.StringIO;

public class MarketingIO implements ElementIO {

    @XmlAttribute
    public Action action;

    @XmlValue
    public StringIO name;

    public StringIO description;

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "blurb", type = AdvertisingBlurbIO.class))
    public List<AdvertisingBlurbIO> blurbs = new ArrayList<AdvertisingBlurbIO>();

    @Override
    public Action getAction() {
        return action;
    }
}
