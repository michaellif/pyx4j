/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.reference.geo.googleapis;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class AddressComponent extends ReflectionBean {

    @XmlElement(name = "long_name")
    public String longName;

    @XmlElement(name = "short_name")
    public String shortName;

    @XmlElements(@XmlElement(name = "type"))
    public List<String> types = new ArrayList<String>();
}
