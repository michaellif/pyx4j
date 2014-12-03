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
package com.propertyvista.portal.prospect.ui.application.components;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.domain.media.ProofOfAssetDocumentFile;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.ProofOfAssetDocumentType;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset.AssetType;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class PersonalAssetFolder extends PortalBoxFolder<CustomerScreeningPersonalAsset> {

    private static final I18n i18n = I18n.get(PersonalAssetFolder.class);

    private ApplicationDocumentationPolicy documentationPolicy;

    public PersonalAssetFolder() {
        this(true);
    }

    public PersonalAssetFolder(boolean editable) {
        super(CustomerScreeningPersonalAsset.class, i18n.tr("Personal Asset"), editable);

        if (editable) {
            setNoDataLabel(i18n.tr("Please enter your asset(s) if present"));
        }
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

        this.addComponentValidator(new AbstractComponentValidator<IList<CustomerScreeningPersonalAsset>>() {
            @Override
            public AbstractValidationError isValid() {
                if (getCComponent().getValue() != null) {
                    if (getCComponent().getValue().size() > 3) {
                        return new BasicValidationError(getCComponent(), i18n.tr("No need to supply more than 3 items"));
                    }
                }
                return null;
            }
        });
    }

    @Override
    protected void addItem() {
        new SelectEnumDialog<AssetType>(i18n.tr("Select Asset Type"), EnumSet.allOf(AssetType.class)) {
            @Override
            public boolean onClickOk() {
                CustomerScreeningPersonalAsset item = EntityFactory.create(CustomerScreeningPersonalAsset.class);
                item.assetType().setValue(getSelectedType());
                addItem(item);
                return true;
            }
        }.show();
    }

    @Override
    protected CForm<CustomerScreeningPersonalAsset> createItemForm(IObject<?> member) {
        return new PersonalAssetEditor();
    }

    private class PersonalAssetEditor extends CForm<CustomerScreeningPersonalAsset> {

        private final ProofOfAssetDocumentFileFolder fileUpload = new ProofOfAssetDocumentFileFolder();

        public PersonalAssetEditor() {
            super(CustomerScreeningPersonalAsset.class);
        }

        public void onSetDocumentationPolicy() {
            displayProofDocsPolicy();
            revalidate();
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().assetType()).decorate().componentWidth(180);
            formPanel.append(Location.Left, proto().ownership()).decorate().componentWidth(60);
            formPanel.append(Location.Left, proto().assetValue()).decorate().componentWidth(100);

            formPanel.h3(i18n.tr("Proof Documents"));
            formPanel.append(Location.Left, proto().files(), fileUpload);

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
            fileUpload.addComponentValidator(new AbstractComponentValidator<IList<ProofOfAssetDocumentFile>>() {
                @Override
                public BasicValidationError isValid() {
                    if (getCComponent().getValue() != null && documentationPolicy != null) {
                        if (documentationPolicy.mandatoryProofOfAsset().getValue(false) && getCComponent().getValue().isEmpty()) {
                            return new BasicValidationError(getCComponent(), i18n.tr("Proof of Asset should be supplied"));
                        }
                    }
                    return null;
                }
            });
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