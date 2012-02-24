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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.crm.client.ui.crud.settings.tax.TaxFolder;
import com.propertyvista.crm.rpc.services.selections.SelectProductItemTypeCrudService;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.policy.dto.ProductTaxPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.ProductTaxPolicyItem;

public class ProductTaxPolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<ProductTaxPolicyDTO> {

    private final static I18n i18n = I18n.get(ProductTaxPolicyEditorForm.class);

    public ProductTaxPolicyEditorForm() {
        this(false);
    }

    public ProductTaxPolicyEditorForm(boolean viewMode) {
        super(ProductTaxPolicyDTO.class, viewMode);
    }

    @Override
    protected List<com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
                new TabDescriptor(createItemsPanel(), i18n.tr("Items"))
        );//@formatter:on
    }

    private Widget createItemsPanel() {
        FormFlexPanel panel = new FormFlexPanel();

        int row = -1;
        panel.setWidget(++row, 0, inject(proto().policyItems(), new ProductTaxPolicyItemFolder(isEditable())));

        return panel;
    }

    private static class ProductTaxPolicyItemFolder extends VistaBoxFolder<ProductTaxPolicyItem> {

        public ProductTaxPolicyItemFolder(boolean modifiable) {
            super(ProductTaxPolicyItem.class, modifiable);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof ProductTaxPolicyItem) {
                return new ProductTaxPolicyItemEditor();
            } else {
                return super.create(member);
            }
        }

        @Override
        protected void addItem() {
            new ProductSelectorDialog().show();
        }

        // internals:

        private static class ProductTaxPolicyItemEditor extends CEntityDecoratableEditor<ProductTaxPolicyItem> {

            public ProductTaxPolicyItemEditor() {
                super(ProductTaxPolicyItem.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();

                int row = -1;
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType())).build());
                get(proto().productItemType()).setViewable(true);

                content.setH2(++row, 0, 1, proto().taxes().getMeta().getCaption());
                content.setWidget(++row, 0, inject(proto().taxes(), new TaxFolder(isEditable())));

                return content;
            }
        }

        List<ProductItemType> getAlreadySelected() {
            List<ProductItemType> selected = new Vector<ProductItemType>();
            for (ProductTaxPolicyItem item : getValue()) {
                if (!item.productItemType().isNull()) {
                    selected.add(item.productItemType());
                }
            }
            return selected;
        }

        private class ProductSelectorDialog extends EntitySelectorDialog<ProductItemType> {

            public ProductSelectorDialog() {
                super(ProductItemType.class, true, getAlreadySelected(), i18n.tr("Select Product"));
                setWidth("700px");
            }

            @Override
            public boolean onClickOk() {
                if (getSelectedItems().isEmpty()) {
                    return false;
                } else {
                    for (ProductItemType selected : getSelectedItems()) {
                        ProductTaxPolicyItem item = EntityFactory.create(ProductTaxPolicyItem.class);
                        item.productItemType().set(selected);
                        addItem(item);
                    }

                    return true;
                }
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().type()).build(),
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().glCode()).build()
                );//@formatter:on
            }

            @Override
            protected AbstractListService<ProductItemType> getSelectService() {
                return GWT.<AbstractListService<ProductItemType>> create(SelectProductItemTypeCrudService.class);
            }
        }
    }
}
