/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.security.shared.Behavior;

@I18n
public enum VistaCrmBehavior implements Behavior {

    /** This is preliminary list */

    PropertyManagement,

    OAPI,

    Mechanicals,

    BuildingFinancial,

    Marketing,

    MarketingMedia,

    Tenants,

    Equifax,

    Emergency,

    ScreeningData,

    Occupancy,

    Maintenance,

    Organization,

    OrganizationFinancial,

    AggregatedTransfer,

    OrganizationPolicy,

    Contacts,

    ProductCatalog,

    Billing,

    Reports,

    /** this behaviour can take control of other's people dashboards */
    DashboardManager,

    //Onboarding
    PropertyVistaAccountOwner,

    PropertyVistaSupport;

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }
}
