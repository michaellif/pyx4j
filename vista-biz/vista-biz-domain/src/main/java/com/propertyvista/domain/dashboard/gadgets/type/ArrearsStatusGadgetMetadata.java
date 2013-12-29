/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2012
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
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.security.VistaCrmBehavior;

/**
 * @deprecated it's gonna be replaced by an 'Arrears' gadget that shows summary and knows to zoom in to details
 */
@Deprecated
@DiscriminatorValue("ArrearsGadgetMeta")
@Transient
@Caption(name = "Arrears Status")
@GadgetDescription(//@formatter:off        
        description = "Shows the information about lease arrears, including how long it is overdue, total balance, legal status information etc. This gadget can either show total arrears or arrears of specific type (i.e. rent, parking or other)",
        keywords = "Arrears",
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
public interface ArrearsStatusGadgetMetadata extends GadgetMetadata, BuildingGadget {

    IPrimitive<Boolean> filterByCategory();

    @Caption(description = "Choose which category of arrears to display")
    @NotNull
    IPrimitive<ARCode.Type> category();

    IPrimitive<Boolean> customizeDate();

    @NotNull
    IPrimitive<LogicalDate> asOf();

    @EmbeddedEntity
    ListerUserSettings arrearsStatusListerSettings();
}
