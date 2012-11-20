/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.NoInsuranceTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.OtherProviderTenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureTenantInsuranceStatusShortDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;

// TODO this is a mockup
public class TenantInsuranceFacadeImpl implements TenantInsuranceFacade {

    public static final File MOCKUP_CONFIG_FILE = new File(System.getProperty("user.dir", ".") + File.separator + "tenant-insurance-facade-mockup.properties");

    private static final I18n i18n = I18n.get(TenantInsuranceFacadeImpl.class);

    @Override
    public TenantInsuranceStatusDTO getInsuranceStatus(Tenant tenantStub) {
        Map<String, String> config;
        try {
            config = PropertiesConfiguration.loadProperties(MOCKUP_CONFIG_FILE);
        } catch (Throwable e) {
            config = new HashMap<String, String>();
        }
        String insuranceProvider = config.get("provider");
        if ("tenantSure".equals(insuranceProvider)) {
            TenantSureTenantInsuranceStatusShortDTO tenantSureStatus = EntityFactory.create(TenantSureTenantInsuranceStatusShortDTO.class);
            tenantSureStatus.liabilityCoverage().setValue(new BigDecimal(config.get("liability")));
            tenantSureStatus.monthlyPremiumPayment().setValue(new BigDecimal(config.get("tenantSure.monthlyPremium")));
            tenantSureStatus.nextPaymentDate().setValue(fetchDate(config, "tenantSure.nextPaymentDate"));
            TenantSureMessageDTO m = tenantSureStatus.messages().$();
            m.message()
                    .setValue(
                            i18n.tr("There was a problem with your last scheduled payment. If you dont update your credit card details until {0,date,short}, your TeantSure insurance will expire on {1,date,short}.",
                                    fetchDate(config, "tenantSure.gracePeriodEndDate"), new LogicalDate()));
            tenantSureStatus.messages().add(m);

            return tenantSureStatus;

        } else if ("other".equals(insuranceProvider)) {
            OtherProviderTenantInsuranceStatusDTO otherProviderStatus = EntityFactory.create(OtherProviderTenantInsuranceStatusDTO.class);
            otherProviderStatus.liabilityCoverage().setValue(new BigDecimal(config.get("liability")));
            otherProviderStatus.expirationDate().setValue(fetchDate(config, "other.insuranceExpirationDate"));
            return otherProviderStatus;

        } else {
            NoInsuranceTenantInsuranceStatusDTO noInsuranceStatus = EntityFactory.create(NoInsuranceTenantInsuranceStatusDTO.class);
            noInsuranceStatus.minimumRequiredLiability().setValue(new BigDecimal("90000000"));
            return noInsuranceStatus;
        }

    }

    private static LogicalDate fetchDate(Map<String, String> config, String propertyName) {
        String rawDate = config.get(propertyName);
        return rawDate != null ? new LogicalDate(DateUtils.detectDateformat(config.get(propertyName))) : null;
    }
}
