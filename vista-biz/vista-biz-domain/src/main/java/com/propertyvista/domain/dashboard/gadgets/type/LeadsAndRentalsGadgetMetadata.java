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
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.security.VistaCrmBehavior;

@DiscriminatorValue("LeadsAndRentalsGadgetMeta")
@Transient
@Caption(name = "Leads vs. Rentals")
@GadgetDescription(//@formatter:off
        description = "Displays comparison of number of leads vs number of appointmends vs number of rentals",
        keywords = { "Leads", "Rentals", "Leases" },
        allowedBehaviors = {
                VistaCrmBehavior.PropertyManagement_OLD,
                VistaCrmBehavior.Mechanicals_OLD,
                VistaCrmBehavior.BuildingFinancial_OLD,
                VistaCrmBehavior.Marketing_OLD,
                VistaCrmBehavior.MarketingMedia_OLD,
                VistaCrmBehavior.Tenants_OLD,
                VistaCrmBehavior.Equifax_OLD,
                VistaCrmBehavior.Emergency_OLD,
                VistaCrmBehavior.ScreeningData_OLD,
                VistaCrmBehavior.Occupancy_OLD,
                VistaCrmBehavior.Maintenance_OLD,
                VistaCrmBehavior.Organization_OLD,
                VistaCrmBehavior.Contacts_OLD,
                VistaCrmBehavior.ProductCatalog_OLD,
                VistaCrmBehavior.Billing_OLD,
                VistaCrmBehavior.Reports_OLD,
                VistaCrmBehavior.PropertyVistaAccountOwner_OLD,
                VistaCrmBehavior.PropertyVistaSupport
        }
)//@formatter:on
public interface LeadsAndRentalsGadgetMetadata extends CounterGadgetBaseMetadata, BuildingGadget {

    ListerUserSettings leadsListerDetails();

    ListerUserSettings appointmentsListerDetails();

    ListerUserSettings leasesFromLeadsListerSettings();

}
