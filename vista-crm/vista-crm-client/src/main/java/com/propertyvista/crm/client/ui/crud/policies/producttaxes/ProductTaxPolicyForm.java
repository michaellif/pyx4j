/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.producttaxes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.administration.financial.tax.TaxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.crm.rpc.services.selections.SelectProductCodeListService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.dto.ProductTaxPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.ProductTaxPolicyItem;

public class ProductTaxPolicyForm extends PolicyDTOTabPanelBasedForm<ProductTaxPolicyDTO> {

    private final static I18n i18n = I18n.get(ProductTaxPolicyForm.class);

    public ProductTaxPolicyForm(IForm<ProductTaxPolicyDTO> view) {
        super(ProductTaxPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createItemsPanel());
    }

    private TwoColumnFlexFormPanel createItemsPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Items"));

        int row = -1;
        panel.setWidget(++row, 0, 2, inject(proto().policyItems(), new ProductTaxPolicyItemFolder(isEditable())));

        return panel;
    }

    private static class ProductTaxPolicyItemFolder extends VistaBoxFolder<ProductTaxPolicyItem> {

        public ProductTaxPolicyItemFolder(boolean modifiable) {
            super(ProductTaxPolicyItem.class, modifiable);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof ProductTaxPolicyItem) {
                return new ProductTaxPolicyItemEditor();
            } else {
                return super.create(member);
            }
        }

        @Override
        protected void addItem() {
            final List<ARCode> alreadySelectedProducts = new ArrayList<ARCode>();
            for (ProductTaxPolicyItem item : getValue()) {
                if (!item.productCode().isNull()) {
                    alreadySelectedProducts.add(item.productCode());
                }
            }

            new ProductSelectorDialog(alreadySelectedProducts).show();
        }

        // internals:

        private static class ProductTaxPolicyItemEditor extends CEntityForm<ProductTaxPolicyItem> {

            public ProductTaxPolicyItemEditor() {
                super(ProductTaxPolicyItem.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

                int row = -1;
                content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().productCode()), true).build());
                get(proto().productCode()).setViewable(true);

                content.setH3(++row, 0, 2, proto().taxes().getMeta().getCaption());
                content.setWidget(++row, 0, 2, inject(proto().taxes(), new TaxFolder(isEditable())));

                return content;
            }
        }

        private class ProductSelectorDialog extends EntitySelectorTableDialog<ARCode> {

            public ProductSelectorDialog(List<ARCode> alreadySelectedProducts) {
                super(ARCode.class, false, alreadySelectedProducts, i18n.tr("Select Product Type"));
                setDialogPixelWidth(700);
            }

            @Override
            public boolean onClickOk() {
                if (getSelectedItems().isEmpty()) {
                    return false;
                } else {
                    for (ARCode selected : getSelectedItems()) {
                        ProductTaxPolicyItem item = EntityFactory.create(ProductTaxPolicyItem.class);
                        item.productCode().set(selected);
                        ProductTaxPolicyItemFolder.this.addItem(item);
                    }

                    return true;
                }
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().glCode()).build()
                );//@formatter:on
            }

            @Override
            public List<Sort> getDefaultSorting() {
                return Arrays.asList(new Sort(proto().name(), false));
            }

            @Override
            protected AbstractListService<ARCode> getSelectService() {
                return GWT.<AbstractListService<ARCode>> create(SelectProductCodeListService.class);
            }
        }
    }
}
