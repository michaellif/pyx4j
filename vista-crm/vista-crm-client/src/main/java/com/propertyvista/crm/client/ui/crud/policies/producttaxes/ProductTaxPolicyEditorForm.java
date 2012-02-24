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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
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

        private static class ProductTaxPolicyItemEditor extends CEntityDecoratableEditor<ProductTaxPolicyItem> {

            private final static I18n i18n = I18n.get(ProductTaxPolicyItemEditor.class);

            public ProductTaxPolicyItemEditor() {
                super(ProductTaxPolicyItem.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                int row = -1;

                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType().name())).build());
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType().type())).build());

                return content;
            }
        }

    }

}
