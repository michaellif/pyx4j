/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 12, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.model;

import com.propertyvista.oapi.model.types.BuildingAmenityTypeIO;
import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.StringIO;

public class BuildingAmenityIO extends AbstractElementIO {

    public BuildingAmenityTypeIO type;

    public StringIO name;

    public StringIO description;

}
