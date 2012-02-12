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
package com.propertyvista.crm.client.ui.crud.policies.charges;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.policy.dto.ChargePolicyDTO;
import com.propertyvista.domain.policy.policies.domain.ChargePolicyItem;

public class ChargePolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<ChargePolicyDTO> {
    private final static I18n i18n = I18n.get(ChargePolicyEditorForm.class);

    public ChargePolicyEditorForm() {
        this(false);
    }

    public ChargePolicyEditorForm(boolean viewMode) {
        super(ChargePolicyDTO.class, viewMode);
    }

    @Override
    protected List<com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
                new TabDescriptor(createItemsPanel(), i18n.tr("Items"))
        );
    }

    private Widget createItemsPanel() {
        FormFlexPanel panel = new FormFlexPanel();
        
        int row = -1;
        
        panel.setWidget(++row, 0, inject(proto().chargePolicyItems(), new ChargePolicyEditorFolder()));

        return panel;
    }
    
    
    private static class ChargePolicyEditorFolder extends VistaBoxFolder<ChargePolicyItem> {

        public ChargePolicyEditorFolder() {
            super(ChargePolicyItem.class);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof ChargePolicyItem) {
                return new ChargePolicyItemEditor();
            } else {
                return super.create(member);
            }
        }

        private static class ChargePolicyItemEditor extends CEntityDecoratableEditor<ChargePolicyItem> {

            public ChargePolicyItemEditor() {
                super(ChargePolicyItem.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                int row = -1;
                
                if (isEditable()) {
                    CComboBox<ProductItemType.Type>  itemTypes = new CComboBox<ProductItemType.Type>();
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType().type())).build()); 
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType().serviceType())).build()); 
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType().featureType())).build()); 
                }

                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType())).build());
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().chargeCode())).build());
  
                return content;
            }
        }

    }

}
