/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.InsuranceGeneralCertificate;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureCertificate;
import com.propertyvista.domain.tenant.lease.Tenant;

public class InsuranceCertificateComparatorTest {

    private long keyCounter;

    @Before
    public void setUp() {
        keyCounter = 0L;
    }

    @Test
    public void testCertificatesOfTenatsInContextComeFirst() {
        Tenant tenantInContext = EntityFactory.create(Tenant.class);
        tenantInContext.setPrimaryKey(genKey());

        Tenant tenantForDiscraction = EntityFactory.create(Tenant.class);
        tenantForDiscraction.setPrimaryKey(genKey());

        List<InsuranceCertificate> certificates = new ArrayList<InsuranceCertificate>(Arrays.asList(//@formatter:off
               makeGeneric(tenantForDiscraction, new BigDecimal("10")),
               makeGeneric(tenantForDiscraction, new BigDecimal("9")),
               makeTenantSure(tenantInContext, new BigDecimal("8")),
               makeGeneric(tenantForDiscraction, new BigDecimal("7")),
               makeGeneric(tenantInContext, new BigDecimal("6"))
        ));//@formatter:on
        sort(tenantInContext, certificates);

        Assert.assertEquals(tenantInContext, certificates.get(0).tenant());
        Assert.assertEquals(tenantInContext, certificates.get(1).tenant());
    }

    @Test
    public void testTenantSureComesFirst() {

        Tenant tenantInContext = EntityFactory.create(Tenant.class);
        tenantInContext.setPrimaryKey(genKey());

        Tenant tenantForDiscraction = EntityFactory.create(Tenant.class);
        tenantForDiscraction.setPrimaryKey(genKey());

        List<InsuranceCertificate> certificates = new ArrayList<InsuranceCertificate>(Arrays.asList(//@formatter:off
               makeGeneric(tenantForDiscraction, new BigDecimal("10")),
               makeGeneric(tenantInContext, new BigDecimal("9")),
               makeTenantSure(tenantInContext, new BigDecimal("8")),
               makeGeneric(tenantForDiscraction, new BigDecimal("7")),
               makeTenantSure(tenantForDiscraction, new BigDecimal("6"))
        ));//@formatter:on
        sort(tenantInContext, certificates);

        Assert.assertEquals(InsuranceTenantSureCertificate.class, certificates.get(0).getInstanceValueClass());
        Assert.assertEquals("the second tenant sure certificate shouldn't be here because it doesn't belong to the tenant in context", InsuranceGeneralCertificate.class,
                certificates.get(1).getInstanceValueClass());
    }

    @Test
    public void testPreferBiggerLiabilityCoverage() {
        Tenant tenantInContext = EntityFactory.create(Tenant.class);
        tenantInContext.setPrimaryKey(genKey());

        Tenant tenantForDistraction = EntityFactory.create(Tenant.class);
        tenantForDistraction.setPrimaryKey(genKey());

        List<InsuranceCertificate> certificates = new ArrayList<InsuranceCertificate>(Arrays.asList(//@formatter:off
                makeGeneric(tenantForDistraction, new BigDecimal("10")),
                makeGeneric(tenantInContext, new BigDecimal("9")),
                makeGeneric(tenantInContext, new BigDecimal("10")),
                makeGeneric(tenantForDistraction, new BigDecimal("7")),
                makeGeneric(tenantForDistraction, new BigDecimal("6"))
         ));//@formatter:on
        sort(tenantInContext, certificates);

        Assert.assertEquals(new BigDecimal("10"), certificates.get(0).liabilityCoverage().getValue());
        Assert.assertEquals(new BigDecimal("9"), certificates.get(1).liabilityCoverage().getValue());
        Assert.assertEquals(new BigDecimal("10"), certificates.get(2).liabilityCoverage().getValue());
        Assert.assertEquals(new BigDecimal("7"), certificates.get(3).liabilityCoverage().getValue());
        Assert.assertEquals(new BigDecimal("6"), certificates.get(4).liabilityCoverage().getValue());

    }

    private InsuranceCertificate makeGeneric(Tenant tenant, BigDecimal liablilityCoverage) {
        InsuranceGeneralCertificate cert = EntityFactory.create(InsuranceGeneralCertificate.class);
        cert.setPrimaryKey(genKey());
        cert.liabilityCoverage().setValue(liablilityCoverage);
        cert.tenant().set(tenant);
        return cert;
    }

    private InsuranceCertificate makeTenantSure(Tenant tenant, BigDecimal liablilityCoverage) {
        InsuranceTenantSureCertificate cert = EntityFactory.create(InsuranceTenantSureCertificate.class);
        cert.setPrimaryKey(genKey());
        cert.liabilityCoverage().setValue(liablilityCoverage);
        cert.tenant().set(tenant);
        return cert;
    }

    private Key genKey() {
        return new Key(++keyCounter);
    }

    private void sort(Tenant tenantInContext, List<InsuranceCertificate> list) {
        java.util.Collections.sort(list, new InsuranceCertificateComparator(tenantInContext));
    }
}
