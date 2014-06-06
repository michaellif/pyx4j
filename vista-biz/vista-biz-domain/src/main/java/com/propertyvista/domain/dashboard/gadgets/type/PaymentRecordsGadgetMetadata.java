/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 29, 2012
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
import com.pyx4j.entity.core.IPrimitiveSet;

import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.VistaCrmBehavior;

@DiscriminatorValue("PaymentRecordsGadgetMetadata")
@Transient
@Caption(name = "Payment Records")
@GadgetDescription(//@formatter:off
        description = "Displays payment records that changed today.",
        keywords = { "Payments", "Collections", "Money"},
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
public interface PaymentRecordsGadgetMetadata extends GadgetMetadata, BuildingGadget {

    /**
     * Payment type filter.
     * When <code>null</code> the gadget should display all payment records.
     */
    IPrimitiveSet<PaymentType> paymentMethodFilter();

    /**
     * Payment status filter.
     */
    IPrimitiveSet<PaymentRecord.PaymentStatus> paymentStatusFilter();

    IPrimitive<Boolean> customizeTargetDate();

    @NotNull
    IPrimitive<LogicalDate> targetDate();

    @EmbeddedEntity
    ListerUserSettings paymentRecordsListerSettings();
}
