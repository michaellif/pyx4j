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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Region {
    private String code;

    private List<City> cities = new ArrayList<City>();

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(code);

        for (City city : cities) {
            sb.append("\n");
            sb.append(city);
        }

        return sb.toString();
    }

    @XmlAttribute
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @XmlElement(name = "city")
    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}
