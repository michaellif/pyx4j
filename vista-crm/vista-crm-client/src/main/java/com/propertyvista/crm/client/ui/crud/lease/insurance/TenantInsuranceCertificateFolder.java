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
package com.propertyvista.crm.client.ui.crud.lease.insurance;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.tenant.insurance.GeneralInsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.PropertyVistaIntegratedInsurance;

public class TenantInsuranceCertificateFolder extends VistaBoxFolder<InsuranceCertificate> {

    private final I18n i18n = I18n.get(TenantInsuranceCertificateFolder.class);

    private final boolean displayTenantOwner;

    public TenantInsuranceCertificateFolder(boolean displayTenantOwner) {
        super(InsuranceCertificate.class);

        setRemovable(true);
        setOrderable(false);

        this.displayTenantOwner = displayTenantOwner;
    }

    @Override
    protected CForm<InsuranceCertificate> createItemForm(IObject<?> member) {
        return new TenantInsuranceCertificateForm<>(InsuranceCertificate.class, displayTenantOwner);
    }

    @Override
    public VistaBoxFolderItemDecorator<InsuranceCertificate> createItemDecorator() {
        VistaBoxFolderItemDecorator<InsuranceCertificate> decorator = super.createItemDecorator();
        decorator.setExpended(isEditable());
        return decorator;
    }

    @Override
    protected void addItem() {
        GeneralInsuranceCertificate certificate = EntityFactory.create(GeneralInsuranceCertificate.class);
        super.addItem(certificate);
    }

    @Override
    protected void removeItem(CFolderItem<InsuranceCertificate> item) {
        if ((item.getValue() instanceof PropertyVistaIntegratedInsurance) || ((IPrimitive<Boolean>) item.getValue().isManagedByTenant()).getValue(false)) {
            MessageDialog.info(i18n.tr("This insurance certificate was uploaded by tenant and cannot be deleted"));
        } else {
            super.removeItem(item);
        }
    }
}
