/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO.PreviousClaims;

public class TenantSureOptionalExtrasFormatterTest {

    private ITenantSureOptionalExtrasFormatter optionalExtrasFormatter;

    private TenantSureCoverageDTO coverageRequest;

    private Tenant tenant;

    public TenantSureOptionalExtrasFormatterTest() {
        this.optionalExtrasFormatter = null;
    }

    @Before
    public void setUp() {
        this.optionalExtrasFormatter = new TenantSureOptionalExtrasFormatter();
        coverageRequest = EntityFactory.create(TenantSureCoverageDTO.class);
        coverageRequest.deductible().setValue(new BigDecimal("500.00"));
        coverageRequest.numberOfPreviousClaims().setValue(PreviousClaims.None);
        coverageRequest.smoker().setValue(false);

        tenant = EntityFactory.create(Tenant.class);
        tenant.lease().currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(new BigDecimal("500.55634"));
        tenant.lease().unit().building().info().hasFireAlarm().setValue(true);
        tenant.lease().unit().building().info().hasSprinklers().setValue(true);
        tenant.lease().unit().building().info().hasEarthquakes().setValue(false);
    }

    @Test
    public void monthlyRevenue() {
        tenant.lease().currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(new BigDecimal("100"));
        Assert.assertThat(optionalExtras(), hasOption("MonthlyRevenue", "100"));

        tenant.lease().currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(new BigDecimal("567"));
        Assert.assertThat(optionalExtras(), hasOption("MonthlyRevenue", "567"));
    }

    @Test
    public void smoker() {
        coverageRequest.smoker().setValue(true);
        Assert.assertThat(optionalExtras(), hasOption("Smoker", "true"));

        coverageRequest.smoker().setValue(false);
        Assert.assertThat(optionalExtras(), hasOption("Smoker", "false"));

        coverageRequest.smoker().setValue(null);
        Assert.assertThat(optionalExtras(), hasOption("Smoker", "false"));
    }

    @Test
    public void claims() {
        coverageRequest.numberOfPreviousClaims().setValue(PreviousClaims.None);
        Assert.assertThat(optionalExtras(), hasOption("Claims", "0"));

        coverageRequest.numberOfPreviousClaims().setValue(PreviousClaims.One);
        Assert.assertThat(optionalExtras(), hasOption("Claims", "1"));

        coverageRequest.numberOfPreviousClaims().setValue(PreviousClaims.Two);
        Assert.assertThat(optionalExtras(), hasOption("Claims", "2"));
    }

    @Test(expected = Error.class)
    public void claimsMustFailWhenMoreThanTwo() {
        coverageRequest.numberOfPreviousClaims().setValue(PreviousClaims.MoreThanTwo);
        optionalExtras();
    }

    @Test
    public void alarm() {
        tenant.lease().unit().building().info().hasFireAlarm().setValue(true);
        Assert.assertThat(optionalExtras(), hasOption("Alarm", "true"));

        tenant.lease().unit().building().info().hasFireAlarm().setValue(false);
        Assert.assertThat(optionalExtras(), hasOption("Alarm", "false"));

        tenant.lease().unit().building().info().hasFireAlarm().setValue(null);
        Assert.assertThat(optionalExtras(), hasOption("Alarm", "false"));
    }

    @Test
    public void sprinklers() {
        tenant.lease().unit().building().info().hasSprinklers().setValue(true);
        Assert.assertThat(optionalExtras(), hasOption("Sprinklers", "true"));

        tenant.lease().unit().building().info().hasSprinklers().setValue(false);
        Assert.assertThat(optionalExtras(), hasOption("Sprinklers", "false"));

        tenant.lease().unit().building().info().hasSprinklers().setValue(null);
        Assert.assertThat(optionalExtras(), hasOption("Sprinklers", "false"));
    }

    @Test
    public void bceq() {
        tenant.lease().unit().building().info().hasEarthquakes().setValue(true);
        Assert.assertThat(optionalExtras(), hasOption("BCEQ", "true"));

        tenant.lease().unit().building().info().hasEarthquakes().setValue(false);
        Assert.assertThat(optionalExtras(), hasOption("BCEQ", "false"));

        tenant.lease().unit().building().info().hasEarthquakes().setValue(null);
        Assert.assertThat(optionalExtras(), hasOption("BCEQ", "false"));
    }

    @Test
    public void deductilbe() {
        coverageRequest.deductible().setValue(new BigDecimal("500.00"));
        Assert.assertThat(optionalExtras(), hasOption("Deductible", "500"));

        coverageRequest.deductible().setValue(new BigDecimal("1000.0000"));
        Assert.assertThat(optionalExtras(), hasOption("Deductible", "1000"));

        coverageRequest.deductible().setValue(new BigDecimal("2500.0000"));
        Assert.assertThat(optionalExtras(), hasOption("Deductible", "2500"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deductibleMustFailWhenTheValueIsInvalid() {
        coverageRequest.deductible().setValue(new BigDecimal("666"));
        optionalExtras();
    }

    private Matcher<List<String>> hasOption(final String key, final String value) {
        return new BaseMatcher<List<String>>() {

            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(Object item) {
                if (item instanceof List) {
                    return ((List<String>) item).contains(key + "=" + value);
                } else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("expected optional extras to contain '" + key + "=" + value + "'");
            }
        };
    }

    private List<String> optionalExtras() {
        return Arrays.asList(optionalExtrasFormatter.formatOptionalExtras(coverageRequest, tenant).split(";"));
    }
}
