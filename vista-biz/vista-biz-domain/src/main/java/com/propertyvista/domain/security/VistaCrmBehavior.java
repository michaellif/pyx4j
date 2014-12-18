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
 */
package com.propertyvista.domain.security;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.security.shared.Behavior;

@I18n
public enum VistaCrmBehavior implements Behavior {

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

    LeaseBasic, LeaseAdvanced, LeaseFull,

    // --

    ApplicationBasic, ApplicationVerifyDoc, ApplicationFull,

    // --

    ApplicationDecisionReserveUnit, ApplicationDecisionStartOnline,

    ApplicationDecisionSubmit, ApplicationDecisionVerify, ApplicationDecisionApprove,

    ApplicationDecisionFull,

    // --

    CreditCheckBasic, CreditCheckFull,

    // --

    TenantBasic, TenantAdvanced, TenantFull,

    // --

    PotentialTenantBasic, PotentialTenantAdvanced, PotentialTenantScreening, PotentialTenantFull,

    // --

    GuarantorBasic, GuarantorAdvanced, GuarantorFull,

    // --

    FinancialMoneyIN, FinancialAggregatedTransfer,

    FinancialBasic, FinancialPayments, FinancialAdvanced, FinancialFull,

    // --

    AccountSelf,

    // --

    EmployeeBasic, EmployeeFull,

    // --

    PortfolioBasic, PortfolioFull,

    // --

    @Translate("Legal & Collections Basic")
    LegalCollectionsBasic,

    @Translate("Legal & Collections Full")
    LegalCollectionsFull,

    // --

    DashboardsGadgetsBasic,

    /** this behaviour can take control of other's people dashboards */
    DashboardsGadgetsFull,

    // --

    // --

    AdminGeneral, AdminFinancial, AdminContent,

    // --

    OAPI_Properties,

    OAPI_ILS,

    // --

    ;

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }
}
