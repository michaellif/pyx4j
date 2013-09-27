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

import java.util.Comparator;

import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.TenantSureInsuranceCertificate;
import com.propertyvista.domain.tenant.lease.Tenant;

public final class InsuranceCertificateComparator implements Comparator<InsuranceCertificate> {
    private final Tenant tenantId;

    InsuranceCertificateComparator(Tenant tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public int compare(InsuranceCertificate o1, InsuranceCertificate o2) {
        if ((o1.insurancePolicy().tenant().equals(tenantId)) && !(o2.insurancePolicy().tenant().equals(tenantId))) {
            return -1;
        } else if (!(o1.insurancePolicy().tenant().equals(tenantId)) && (o2.insurancePolicy().tenant().equals(tenantId))) {
            return 1;
        } else if ((o1.getInstanceValueClass().equals(TenantSureInsuranceCertificate.class))
                && !(o2.getInstanceValueClass().equals(TenantSureInsuranceCertificate.class))) {
            return -1;
        } else if (!(o1.getInstanceValueClass().equals(TenantSureInsuranceCertificate.class))
                && (o2.getInstanceValueClass().equals(TenantSureInsuranceCertificate.class))) {
            return 1;
        } else {
            return -((Comparable) o1.liabilityCoverage().getValue()).compareTo(o2.liabilityCoverage().getValue());
        }
    }
}