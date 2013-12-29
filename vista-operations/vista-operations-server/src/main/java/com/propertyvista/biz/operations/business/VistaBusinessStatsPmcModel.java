/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.operations.business;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface VistaBusinessStatsPmcModel extends IEntity {

    @Caption(name = "PMC Name")
    IPrimitive<String> name();

    IPrimitive<Boolean> active();

    IPrimitive<Date> lastLogin();

    // PMC stats

    IPrimitive<String> country();

    IPrimitive<Integer> buildingCount();

    IPrimitive<Integer> newBuildingCount();

    IPrimitive<Integer> unitsCount();

    // Tenants

    IPrimitive<Integer> tenantsCount();

    IPrimitive<Integer> newTenantsCount();

    IPrimitive<Integer> registeredTenantsCount();

    // Electronic payments

    IPrimitive<Integer> payingTenants();

    IPrimitive<Integer> newPayingTenants();

    IPrimitive<Integer> eChequeCount();

    IPrimitive<Integer> eChequeCountOneTime();

    IPrimitive<BigDecimal> eChequeValue();

    IPrimitive<BigDecimal> eChequeValueOneTime();

    IPrimitive<Integer> directBankingCount();

    IPrimitive<BigDecimal> directBankingValue();

    IPrimitive<Integer> interacCount();

    IPrimitive<BigDecimal> interacValue();

    IPrimitive<Integer> creditVisaCount();

    IPrimitive<Integer> creditVisaCountOneTime();

    IPrimitive<BigDecimal> creditVisaValue();

    IPrimitive<BigDecimal> creditVisaValueOneTime();

    IPrimitive<Integer> creditMastercardCount();

    IPrimitive<Integer> creditMastercardCountOneTime();

    IPrimitive<BigDecimal> creditMastercardValue();

    IPrimitive<BigDecimal> creditMastercardValueOneTime();

    IPrimitive<Integer> creditVisaDebitCount();

    IPrimitive<Integer> creditVisaDebitCountOneTime();

    IPrimitive<BigDecimal> creditVisaDebitValue();

    IPrimitive<BigDecimal> creditVisaDebitValueOneTime();

    // TenantSure

    IPrimitive<Integer> insuranceCount();

    IPrimitive<Integer> newInsurance();

    IPrimitive<Integer> otherInsurance();

    // Equifax

    IPrimitive<Integer> processedReports();

    IPrimitive<BigDecimal> reportCost();

    // Details

    IPrimitive<String> contactName();

    IPrimitive<String> contactEmail();

    IPrimitive<String> contactPhone();

}
