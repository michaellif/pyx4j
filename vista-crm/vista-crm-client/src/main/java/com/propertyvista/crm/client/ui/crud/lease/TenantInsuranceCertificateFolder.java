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

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.tenantinsurance.TenantInsuranceCertificateForm;
import com.propertyvista.common.client.ui.components.tenantinsurance.TenantInsuranceCertificateForm.TenantOwnerClickHandler;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.dto.TenantInsuranceCertificateDTO;

public class TenantInsuranceCertificateFolder extends VistaBoxFolder<TenantInsuranceCertificateDTO> {

    private final I18n i18n = I18n.get(TenantInsuranceCertificateDTO.class);

    private final TenantOwnerClickHandler tenantOwnerClickHanlder;

    public TenantInsuranceCertificateFolder(TenantOwnerClickHandler tenatOwnerClickHandler) {
        super(TenantInsuranceCertificateDTO.class);
        setRemovable(true);
        setOrderable(false);
        this.tenantOwnerClickHanlder = tenatOwnerClickHandler;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof InsuranceCertificate) {
            TenantInsuranceCertificateForm form = new TenantInsuranceCertificateForm(TenantInsuranceCertificateDTO.class, tenantOwnerClickHanlder != null,
                    tenantOwnerClickHanlder);
            return form;
        }
        return super.create(member);
    }

    @Override
    public IFolderItemDecorator<TenantInsuranceCertificateDTO> createItemDecorator() {
        BoxFolderItemDecorator<TenantInsuranceCertificateDTO> decorator = (BoxFolderItemDecorator<TenantInsuranceCertificateDTO>) super.createItemDecorator();
        decorator.setExpended(isEditable());
        return decorator;
    }

    @Override
    protected void addItem() {
        TenantInsuranceCertificateDTO certificate = EntityFactory.create(TenantInsuranceCertificateDTO.class);
        certificate.documents().add(certificate.documents().$());
        super.addItem(certificate);
    }

    @Override
    protected void removeItem(CEntityFolderItem<TenantInsuranceCertificateDTO> item) {
        if (item.getValue().isPropertyVistaIntegratedProvider().isBooleanTrue()) {
            // TODO this is ugly
            throw new UserRuntimeException(i18n.tr("This insurance was uploaded by tenant: delete not permitted!"));
        } else {
            super.removeItem(item);
        }
    }
}
