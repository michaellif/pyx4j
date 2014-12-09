/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.common.components;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.ProofOfAssetDocumentType;
import com.propertyvista.domain.tenant.income.CustomerScreeningAsset;
import com.propertyvista.domain.tenant.income.CustomerScreeningAsset.AssetType;
import com.propertyvista.misc.VistaTODO;

public class PersonalAssetFolder extends VistaBoxFolder<CustomerScreeningAsset> {

    private static final I18n i18n = I18n.get(PersonalAssetFolder.class);

    private ApplicationDocumentationPolicy documentationPolicy;

    public PersonalAssetFolder(boolean modifyable) {
        super(CustomerScreeningAsset.class, modifyable);
    }

    public void setPolicyEntity(IEntity parentEntity) {
        ClientPolicyManager.obtainHierarchicalEffectivePolicy(parentEntity, ApplicationDocumentationPolicy.class,
                new DefaultAsyncCallback<ApplicationDocumentationPolicy>() {
                    @Override
                    public void onSuccess(ApplicationDocumentationPolicy result) {
                        setDocumentationPolicy(result);
                    }
                });
    }

    public void setDocumentationPolicy(ApplicationDocumentationPolicy policy) {
        this.documentationPolicy = policy;
        for (CComponent<?, ?, ?, ?> item : getComponents()) {
            ((PersonalAssetEditor) ((CFolderItem<?>) item).getComponents().iterator().next()).onSetDocumentationPolicy();
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        // waiting for 'soft mode' validation!
        if (!VistaTODO.VISTA_4498_Remove_Unnecessary_Validation_Screening_CRM) {
            // get validation logic from portal
        }

    }

    @Override
    protected void addItem() {
        new SelectEnumDialog<AssetType>(i18n.tr("Select Asset Type"), EnumSet.allOf(AssetType.class)) {
            @Override
            public boolean onClickOk() {
                CustomerScreeningAsset item = EntityFactory.create(CustomerScreeningAsset.class);
                item.assetType().setValue(getSelectedType());
                addItem(item);
                return true;
            }
        }.show();
    }

    @Override
    protected CForm<CustomerScreeningAsset> createItemForm(IObject<?> member) {
        return new PersonalAssetEditor();
    }

    private class PersonalAssetEditor extends CForm<CustomerScreeningAsset> {

        private final ProofOfAssetDocumentFileFolder fileUpload = new ProofOfAssetDocumentFileFolder();

        public PersonalAssetEditor() {
            super(CustomerScreeningAsset.class);
        }

        public void onSetDocumentationPolicy() {
            displayProofDocsPolicy();
            revalidate();
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().assetType()).decorate();
            formPanel.append(Location.Left, proto().assetValue()).decorate().componentWidth(100);
            formPanel.append(Location.Left, proto().ownership()).decorate().componentWidth(50);

            formPanel.h3(i18n.tr("Proof Documents"));
            formPanel.append(Location.Dual, proto().files(), fileUpload);

            return formPanel;
        }

        private void displayProofDocsPolicy() {
            fileUpload.setNote(null);

            if (getValue() != null && documentationPolicy != null) {
                for (ProofOfAssetDocumentType item : documentationPolicy.allowedAssetDocuments()) {
                    if (item.assetType().getValue().equals(getValue().assetType().getValue())) {
                        fileUpload.setNote(item.notes().getValue());
                        break;
                    }
                }
            }
        }

        @Override
        public void addValidations() {

            // waiting for 'soft mode' validation!
            if (!VistaTODO.VISTA_4498_Remove_Unnecessary_Validation_Screening_CRM) {
                // get validation logic from portal
            }
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            displayProofDocsPolicy();

            if (getValue().ownership().isNull()) {
                get(proto().ownership()).setValue(BigDecimal.ONE);
            }
        }
    }
}