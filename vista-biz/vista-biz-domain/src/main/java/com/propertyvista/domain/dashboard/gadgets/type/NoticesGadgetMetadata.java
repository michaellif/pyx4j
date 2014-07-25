/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.CounterGadgetBaseMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.security.VistaCrmBehavior;

@DiscriminatorValue("Notices Gadget")
@Transient
@Caption(name = "Notices")
@GadgetDescription(//@formatter:off
        description = "Display the number of notices grouped by move-out date, and lets view their details",
        keywords = { "Leases", "Notices" },
        allowedBehaviors = {
                VistaCrmBehavior.DashboardsGadgets
        }
)//@formatter:on
public interface NoticesGadgetMetadata extends CounterGadgetBaseMetadata, BuildingGadget {

    @EmbeddedEntity
    ListerUserSettings leaseLeasterDetails();

    @EmbeddedEntity
    ListerUserSettings unitsListerSettings();

}
