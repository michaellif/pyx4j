/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
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

@DiscriminatorValue("LeadsAndRentalsGadgetMeta")
@Caption(name = "Leads and Rentals")
@Transient
@GadgetDescription(name = "Leads vs. Rentals", keywords = { "Leads", "Rentals", "Leases" })
public interface LeadsAndRentalsGadgetMetadata extends CounterGadgetBaseMetadata, BuildingGadget {

}
