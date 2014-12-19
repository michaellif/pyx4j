/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.policies.applicationdocumentation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ApplicationDocumentationPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.policy.policies.domain.ProofOfAssetDocumentType;
import com.propertyvista.domain.policy.policies.domain.ProofOfEmploymentDocumentType;
import com.propertyvista.domain.policy.policies.domain.ProofOfIncomeDocumentType;
import com.propertyvista.domain.tenant.income.CustomerScreeningAsset.AssetType;
import com.propertyvista.domain.tenant.income.IncomeSource;

public class ApplicationDocumentationPolicyForm extends PolicyDTOTabPanelBasedForm<ApplicationDocumentationPolicyDTO> {

    private static final I18n i18n = I18n.get(ApplicationDocumentationPolicyForm.class);

    public ApplicationDocumentationPolicyForm(IPrimeFormView<ApplicationDocumentationPolicyDTO, ?> view) {
        super(ApplicationDocumentationPolicyDTO.class, view);

        addTab(createIdentificationDocsTab(), i18n.tr("Required IDs"));
        addTab(createEmploymentDocsTab(), i18n.tr("Employment"));
        addTab(createIncomeDocsTab(), i18n.tr("Income"));
        addTab(createAssetDocsTab(), i18n.tr("Assets"));
    }

    private IsWidget createIdentificationDocsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().numberOfRequiredIDs()).decorate().labelWidth(250).componentWidth(50);

        formPanel.h3(proto().allowedIDs().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().allowedIDs(), new IdentificationDocumentFolder());

        return formPanel;
    }

    private IsWidget createEmploymentDocsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().mandatoryProofOfEmployment()).decorate().labelWidth(250).componentWidth(110);

        formPanel.h3(proto().allowedEmploymentDocuments().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().allowedEmploymentDocuments(), new EmploymentDocumentFolder());

        return formPanel;
    }

    private IsWidget createIncomeDocsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().mandatoryProofOfIncome()).decorate().labelWidth(250).componentWidth(110);

        formPanel.h3(proto().allowedIncomeDocuments().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().allowedIncomeDocuments(), new IncomeDocumentFolder());

        return formPanel;
    }

    private IsWidget createAssetDocsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().mandatoryProofOfAsset()).decorate().labelWidth(250).componentWidth(110);

        formPanel.h3(proto().allowedAssetDocuments().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().allowedAssetDocuments(), new AssetDocumentFolder());

        return formPanel;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().numberOfRequiredIDs()).addComponentValidator(new AbstractComponentValidator<Integer>() {
            @Override
            public BasicValidationError isValid() {
                if (getCComponent().getValue() != null && getCComponent().getValue() == 0) {
                    return new BasicValidationError(getCComponent(), i18n.tr("At least one ID is required"));
                }
                return null;
            }
        });
        get(proto().numberOfRequiredIDs()).addValueChangeHandler(new RevalidationTrigger<Integer>(get(proto().allowedIDs())));
        get(proto().allowedIDs()).addComponentValidator(new AbstractComponentValidator<List<IdentificationDocumentType>>() {
            @Override
            public AbstractValidationError isValid() {
                if (getCComponent().getValue() != null) {
                    if (getCComponent().getValue().size() < getValue().numberOfRequiredIDs().getValue(0)) {
                        return new BasicValidationError(getCComponent(), i18n.tr("The number of allowed IDs can't be less then the number of required  ones"));
                    }
                }
                return null;
            }
        });
    }

    private class IdentificationDocumentFolder extends VistaBoxFolder<IdentificationDocumentType> {

        public IdentificationDocumentFolder() {
            super(IdentificationDocumentType.class);
        }

        @Override
        protected void addItem() {
            new SelectEnumDialog<IdentificationDocumentType.Type>(i18n.tr("Select Document Type"), EnumSet.allOf(IdentificationDocumentType.Type.class)) {
                @Override
                public boolean onClickOk() {
                    IdentificationDocumentType item = EntityFactory.create(IdentificationDocumentType.class);

                    item.type().setValue(getSelectedType());
                    item.name().setValue(getSelectedType().toString());

                    addItem(item);
                    return true;
                }
            }.show();
        }

        @Override
        protected CForm<? extends IdentificationDocumentType> createItemForm(IObject<?> member) {
            return new CForm<IdentificationDocumentType>(IdentificationDocumentType.class) {
                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Left, proto().type()).decorate();
                    formPanel.append(Location.Right, proto().importance()).decorate();

                    formPanel.append(Location.Dual, proto().name()).decorate();
                    formPanel.append(Location.Dual, proto().notes()).decorate();

                    return formPanel;
                }

                @Override
                protected void onValueSet(boolean populate) {
                    super.onValueSet(populate);

                    get(proto().notes()).setVisible(isEditable() || !getValue().notes().isNull());
                }
            };
        }
    }

    private class EmploymentDocumentFolder extends VistaBoxFolder<ProofOfEmploymentDocumentType> {

        public EmploymentDocumentFolder() {
            super(ProofOfEmploymentDocumentType.class);
        }

        @Override
        protected void addItem() {
            EnumSet<IncomeSource> values = IncomeSource.employment();
            List<IncomeSource> usedOnes = new ArrayList<IncomeSource>();
            for (ProofOfEmploymentDocumentType item : getValue()) {
                usedOnes.add(item.incomeSource().getValue());
            }
            values.removeAll(usedOnes);
            new SelectEnumDialog<IncomeSource>(i18n.tr("Select Employment Type"), values) {
                @Override
                public boolean onClickOk() {
                    ProofOfEmploymentDocumentType item = EntityFactory.create(ProofOfEmploymentDocumentType.class);
                    item.incomeSource().setValue(getSelectedType());
                    addItem(item);
                    return true;
                }
            }.show();
        }

        @Override
        protected CForm<? extends ProofOfEmploymentDocumentType> createItemForm(IObject<?> member) {
            return new CForm<ProofOfEmploymentDocumentType>(ProofOfEmploymentDocumentType.class) {
                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Dual, proto().incomeSource()).decorate();
                    formPanel.append(Location.Dual, proto().notes()).decorate();

                    return formPanel;
                }
            };
        }
    }

    private class IncomeDocumentFolder extends VistaBoxFolder<ProofOfIncomeDocumentType> {

        public IncomeDocumentFolder() {
            super(ProofOfIncomeDocumentType.class);
        }

        @Override
        protected void addItem() {
            EnumSet<IncomeSource> values = IncomeSource.otherIncome();
            List<IncomeSource> usedOnes = new ArrayList<IncomeSource>();
            for (ProofOfIncomeDocumentType item : getValue()) {
                usedOnes.add(item.incomeSource().getValue());
            }
            values.removeAll(usedOnes);
            new SelectEnumDialog<IncomeSource>(i18n.tr("Select Income Source"), values) {
                @Override
                public boolean onClickOk() {
                    ProofOfIncomeDocumentType item = EntityFactory.create(ProofOfIncomeDocumentType.class);
                    item.incomeSource().setValue(getSelectedType());
                    addItem(item);
                    return true;
                }
            }.show();
        }

        @Override
        protected CForm<? extends ProofOfIncomeDocumentType> createItemForm(IObject<?> member) {
            return new CForm<ProofOfIncomeDocumentType>(ProofOfIncomeDocumentType.class) {
                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Dual, proto().incomeSource()).decorate();
                    formPanel.append(Location.Dual, proto().notes()).decorate();

                    return formPanel;
                }
            };
        }
    }

    private class AssetDocumentFolder extends VistaBoxFolder<ProofOfAssetDocumentType> {

        public AssetDocumentFolder() {
            super(ProofOfAssetDocumentType.class);
        }

        @Override
        protected void addItem() {
            EnumSet<AssetType> values = EnumSet.allOf(AssetType.class);
            List<AssetType> usedOnes = new ArrayList<AssetType>();
            for (ProofOfAssetDocumentType item : getValue()) {
                usedOnes.add(item.assetType().getValue());
            }
            values.removeAll(usedOnes);
            new SelectEnumDialog<AssetType>(i18n.tr("Select Asset Type"), values) {
                @Override
                public boolean onClickOk() {
                    ProofOfAssetDocumentType item = EntityFactory.create(ProofOfAssetDocumentType.class);
                    item.assetType().setValue(getSelectedType());
                    addItem(item);
                    return true;
                }
            }.show();
        }

        @Override
        protected CForm<? extends ProofOfAssetDocumentType> createItemForm(IObject<?> member) {
            return new CForm<ProofOfAssetDocumentType>(ProofOfAssetDocumentType.class) {
                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Dual, proto().assetType()).decorate();
                    formPanel.append(Location.Dual, proto().notes()).decorate();

                    return formPanel;
                }
            };
        }
    }
}
