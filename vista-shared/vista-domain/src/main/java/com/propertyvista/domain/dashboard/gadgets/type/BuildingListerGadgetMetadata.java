/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 1, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.ListerGadgetBaseMetadata;

@DiscriminatorValue("BuildingLister")
@Transient
@GadgetDescription(//@formatter:off
        name = "Listing of Buildings",
        description = "Table-list-like gadget which displays building data according to prefered rules. Query and display data can be set up",
        keywords = "Buildings"
)//@formatter:on
public interface BuildingListerGadgetMetadata extends ListerGadgetBaseMetadata {

}
