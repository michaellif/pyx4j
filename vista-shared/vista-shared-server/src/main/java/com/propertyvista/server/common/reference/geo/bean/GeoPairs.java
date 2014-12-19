/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 23, 2011
 * @author dmitry
 */
package com.propertyvista.server.common.reference.geo.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GeoPairs {

    private List<GeoPair> pairs = new ArrayList<GeoPair>();

    @XmlElement(name = "pair")
    public List<GeoPair> getPairs() {
        return pairs;
    }

    public void setPairs(List<GeoPair> pairs) {
        this.pairs = pairs;
    }
}
