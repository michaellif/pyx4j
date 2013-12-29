/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.HasCustomizableDateGadgetMetadata;
import com.propertyvista.domain.security.VistaCrmBehavior;

@DiscriminatorValue("UnitAvailbilitySummaryGadgetMetadata")
@Transient
@Caption(name = "Unit Availability Summary")
@GadgetDescription(//@formatter:off
        description = "Shows a summary of information about all units, including the total number of units, vacancy, notice and net exposure information in both percentages and quantity",
        keywords = {"Units", "Availability", "Occupancy", "Vacancy"},
        allowedBehaviors = {
                VistaCrmBehavior.PropertyManagement,
                VistaCrmBehavior.Mechanicals,
                VistaCrmBehavior.BuildingFinancial,
                VistaCrmBehavior.Marketing,
                VistaCrmBehavior.MarketingMedia,
                VistaCrmBehavior.Tenants,
                VistaCrmBehavior.Equifax,
                VistaCrmBehavior.Emergency,
                VistaCrmBehavior.ScreeningData,
                VistaCrmBehavior.Occupancy,
                VistaCrmBehavior.Maintenance,
                VistaCrmBehavior.Organization,
                VistaCrmBehavior.Contacts,
                VistaCrmBehavior.ProductCatalog,
                VistaCrmBehavior.Billing,
                VistaCrmBehavior.Reports,
                VistaCrmBehavior.PropertyVistaAccountOwner,
                VistaCrmBehavior.PropertyVistaSupport
        }
)//@formatter:on
public interface UnitAvailabilitySummaryGadgetMetadata extends GadgetMetadata, HasCustomizableDateGadgetMetadata, BuildingGadget {

}
