/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.CounterGadgetBaseMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;

@DiscriminatorValue("Maintenance Gadget Metadata")
@Caption(name = "Maintenance - Work Orders")
@Transient
@GadgetDescription(keywords = { "Maintenance", "Work Orders" })
public interface MaintenanceGadgetMetadata extends CounterGadgetBaseMetadata, BuildingGadget {

}
