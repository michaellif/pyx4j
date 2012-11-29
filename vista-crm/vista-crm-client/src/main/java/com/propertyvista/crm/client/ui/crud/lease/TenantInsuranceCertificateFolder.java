/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-29
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.tenantinsurance.TenantInsuranceByOtherProviderDetailsForm;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;

public class TenantInsuranceCertificateFolder extends VistaBoxFolder<InsuranceCertificate> {

    public TenantInsuranceCertificateFolder() {
        super(InsuranceCertificate.class);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof InsuranceCertificate) {
            return new TenantInsuranceByOtherProviderDetailsForm();
        }
        return super.create(member);
    }

}
