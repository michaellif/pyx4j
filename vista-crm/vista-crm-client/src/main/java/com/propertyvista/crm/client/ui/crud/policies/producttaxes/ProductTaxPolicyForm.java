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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
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
        addTab(createItemsPanel(), i18n.tr("Items"));

    }

    private IsWidget createItemsPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().policyItems(), new ProductTaxPolicyItemFolder(isEditable()));

        return formPanel;
    }

    class ProductTaxPolicyItemFolder extends VistaBoxFolder<ProductTaxPolicyItem> {

        public ProductTaxPolicyItemFolder(boolean modifiable) {
            super(ProductTaxPolicyItem.class, modifiable);
        }

        @Override
        protected CForm<ProductTaxPolicyItem> createItemForm(IObject<?> member) {
            return new ProductTaxPolicyItemEditor();
        }

        @Override
        protected void addItem() {
            final Set<ARCode> alreadySelectedProducts = new HashSet<>();
            for (ProductTaxPolicyItem item : getValue()) {
                if (!item.productCode().isNull()) {
                    alreadySelectedProducts.add(item.productCode());
                }
            }

            new ProductSelectorDialog(ProductTaxPolicyForm.this.getParentView(), alreadySelectedProducts).show();
        }

        // internals:

        class ProductTaxPolicyItemEditor extends CForm<ProductTaxPolicyItem> {

            public ProductTaxPolicyItemEditor() {
                super(ProductTaxPolicyItem.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().productCode()).decorate();
                get(proto().productCode()).setViewable(true);

                formPanel.h3(proto().taxes().getMeta().getCaption());
                formPanel.append(Location.Left, proto().taxes(), new TaxFolder(ProductTaxPolicyForm.this));

                return formPanel;
            }
        }

        private class ProductSelectorDialog extends EntitySelectorTableVisorController<ARCode> {

            public ProductSelectorDialog(IPane parentView, Set<ARCode> alreadySelectedProducts) {
                super(parentView, ARCode.class, false, alreadySelectedProducts, i18n.tr("Select Product Type"));
            }

            @Override
            public void onClickOk() {
                for (ARCode selected : getSelectedItems()) {
                    ProductTaxPolicyItem item = EntityFactory.create(ProductTaxPolicyItem.class);
                    item.productCode().set(selected);
                    ProductTaxPolicyItemFolder.this.addItem(item);
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
            protected AbstractListCrudService<ARCode> getSelectService() {
                return GWT.<AbstractListCrudService<ARCode>> create(SelectProductCodeListService.class);
            }
        }
    }
}
