/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.security.VistaCrmBehavior;

@Transient
@Caption(name = "Building Resident Insurance Coverage")
@GadgetDescription(//@formatter:off
        description = "Displays insurance coverage of buildings",
        keywords = {"Insurance"},
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
public interface BuildingResidentInsuranceCoverageGadgetMetadata extends GadgetMetadata, BuildingGadget {

    ListerUserSettings buildingInsuranceCoverageListerSettings();

}
