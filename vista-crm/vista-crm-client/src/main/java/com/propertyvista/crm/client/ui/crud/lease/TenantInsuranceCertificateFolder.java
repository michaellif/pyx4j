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
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.tenantinsurance.TenantInsuranceCertificateForm;
import com.propertyvista.common.client.ui.components.tenantinsurance.TenantInsuranceCertificateForm.TenantOwnerClickHandler;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;

public class TenantInsuranceCertificateFolder extends VistaBoxFolder<InsuranceCertificate> {

    private final TenantOwnerClickHandler tenantOwnerClickHanlder;

    public TenantInsuranceCertificateFolder(TenantOwnerClickHandler tenatOwnerClickHandler) {
        super(InsuranceCertificate.class);
        this.tenantOwnerClickHanlder = tenatOwnerClickHandler;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof InsuranceCertificate) {
            TenantInsuranceCertificateForm form = new TenantInsuranceCertificateForm(tenantOwnerClickHanlder != null, tenantOwnerClickHanlder);
            return form;
        }
        return super.create(member);
    }

    @Override
    public IFolderItemDecorator<InsuranceCertificate> createItemDecorator() {
        BoxFolderItemDecorator<InsuranceCertificate> decorator = (BoxFolderItemDecorator<InsuranceCertificate>) super.createItemDecorator();
        decorator.setExpended(false);
        return decorator;
    }
}
