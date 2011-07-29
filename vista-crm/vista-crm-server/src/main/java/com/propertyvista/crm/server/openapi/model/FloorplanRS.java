/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 28, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.crm.server.openapi.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "floorplan")
public class FloorplanRS {

    public String name;

    public String description;

    public Integer floorCount;

    public Double bedrooms;

    public Double bathrooms;

    @XmlElement(name = "medias")
    public MediasRS medias = new MediasRS();
}
