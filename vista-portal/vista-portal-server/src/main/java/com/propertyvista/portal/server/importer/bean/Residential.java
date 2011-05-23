/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 20, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.importer.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Residential {

    List<Region> regions = new ArrayList<Region>();

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(regions.size()).append(" regions");

        for (Region region : regions) {
            sb.append("\n");
            sb.append(region);
        }

        return sb.toString();
    }

    @XmlElement(name = "region")
    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }
}
