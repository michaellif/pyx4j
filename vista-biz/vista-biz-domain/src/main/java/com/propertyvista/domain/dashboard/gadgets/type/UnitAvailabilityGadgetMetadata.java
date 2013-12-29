/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2011
 * @author Artyom
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
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.security.VistaCrmBehavior;

@DiscriminatorValue("UnitAvailability")
@Transient
@Caption(name = "Unit Availability")
@GadgetDescription(//@formatter:off
        description = "Shows the information about units, whether they are available or rented, how long they have been vacant for and revenue lost as a result. Can be customized to show various information about buildings and units, for example their physical condition.",
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
public interface UnitAvailabilityGadgetMetadata extends GadgetMetadata, BuildingGadget {

    @I18n
    enum FilterPreset {

        Vacant,

        Notice,

        @Translate("Vacant/Notice")
        VacantAndNotice,

        Rented,

        NetExposure;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    /** Defines the filtering criteria button by its caption */
    @NotNull
    IPrimitive<FilterPreset> filterPreset();

    IPrimitive<Boolean> customizeDate();

    /** <code>null</code> means now */
    IPrimitive<LogicalDate> asOf();

    @EmbeddedEntity
    ListerUserSettings unitStatusListerSettings();

}
