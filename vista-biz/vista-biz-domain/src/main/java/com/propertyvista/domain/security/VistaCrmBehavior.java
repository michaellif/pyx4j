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
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.security.shared.Behavior;

@I18n
public enum VistaCrmBehavior implements Behavior {

    /** This is preliminary list */

    PropertyManagement_OLD,

    OAPI,

    ILS_OLD,

    Mechanicals_OLD,

    BuildingFinancial_OLD,

    Marketing_OLD,

    MarketingMedia_OLD,

    Tenants_OLD,

    Equifax_OLD,

    Emergency_OLD,

    ScreeningData_OLD,

    Occupancy_OLD,

    Maintenance_OLD,

    Organization_OLD,

    OrganizationFinancial_OLD,

    AggregatedTransfer_OLD,

    OrganizationPolicy_OLD,

    Contacts_OLD,

    ProductCatalog_OLD,

    Billing_OLD,

    Reports_OLD,

    /** this behaviour can take control of other's people dashboards */
    DashboardManager_OLD,

    //Onboarding
    PropertyVistaAccountOwner_OLD,

    Commandant_OLD,

    MessageGroup_OLD,

    PropertyVistaSupport,

    /***************** this is new List **************** */

    BuildingBasic,

    BuildingFinancial,

    BuildingAccounting,

    BuildingProperty,

    BuildingMarketing,

    BuildingMechanicals,

    BuildingAdministrator,

    BuildingLeasing,

    // --

    YardiLoads,

    // --

    MaintenanceBasic, MaintenanceAdvanced, MaintenanceFull,

    // --

    LeasesBasic, LeasesAdvance, LeasesFull,

    // --

    ApllicationBasic, ApllicationAdvance, ApplicationVerifyDoc, ApllicationFull,

    // --

    ApplicationDecisionRecommendationApprove,

    ApplicationDecisionRecommendationFurtherInformation,

    ApplicationDecisionAll,

    // --

    CreditCheckBasic, CreditCheckFull,

    // --

    // --

    // --

    FinancialMoneyIN, FinancialAggregatedTransfer, FinancialPayments, FinancialFull,

    // --

    EmployeeDefault, EmployeeAdvance, EmployeeFull,

    // --

    PortfolioBasic, PortfolioFull,

    // --

    @Translate("Legal & Collections Basic")
    LegalCollectionsBasic,

    @Translate("Legal & Collections Full")
    LegalCollectionsFull,

    // --

    // --

    // --

    ;

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }
}
