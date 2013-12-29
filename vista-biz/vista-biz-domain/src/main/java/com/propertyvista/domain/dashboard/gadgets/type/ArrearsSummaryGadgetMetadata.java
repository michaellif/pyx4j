/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.CounterGadgetBaseMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.security.VistaCrmBehavior;

@DiscriminatorValue("Arrears Gadget Metadata")
@Transient
@Caption(name = "Arrears Summary")
@GadgetDescription(//@formatter:off       
        description = "Displays summary of arrears, has an option to zoom-in to view list of relevant leases.",
        keywords = { "Arrears", "Collections" },
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
public interface ArrearsSummaryGadgetMetadata extends CounterGadgetBaseMetadata, BuildingGadget {

    IPrimitive<Boolean> customizeDate();

    /** <code>null</code> means now */
    @NotNull
    IPrimitive<LogicalDate> asOf();

    IPrimitive<Boolean> customizeCategory();

    @Caption(description = "Choose which category of arrears to display")
    @NotNull
    IPrimitive<ARCode.Type> category();

    @EmbeddedEntity
    ListerUserSettings delinquentLeasesListerSettings();
}
