/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 23, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.v1.service.marketing.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.propertyvista.oapi.v1.model.BuildingInfoIO;

@XmlRootElement
public class PropertyList {
    @XmlElementRef
    public List<PropertyListItem> items = new ArrayList<>();

    @XmlRootElement
    public static class PropertyListItem {
        public String propertyId;

        public BuildingInfoIO propertyInfo;
    }
}
