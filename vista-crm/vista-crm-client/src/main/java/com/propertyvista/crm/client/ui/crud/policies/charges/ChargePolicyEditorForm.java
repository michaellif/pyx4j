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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.Service;
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
            private CComponent itemTypeCb;
            private CComponent serviceTypesCb;
            private CComponent featureTypeCb;
            private CEntityComboBox<ProductItemType> productItemTypeCb;
            private CComponent chargeCodeCb;
            
            
            public ChargePolicyItemEditor() {
                super(ChargePolicyItem.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                int row = -1;
                
                if (isEditable()) {
                    WidgetDecorator wd = new DecoratorBuilder(inject(proto().productItemType().type())).build();
                    itemTypeCb = wd.getComnponent();
                    ((CComboBox<ProductItemType.Type>)itemTypeCb).addValueChangeHandler(new ValueChangeHandler<ProductItemType.Type>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<ProductItemType.Type> event) {
                            productItemTypeCb.resetCriteria();
                            
                            if (event.getValue() == ProductItemType.Type.service)
                            {
                                productItemTypeCb.addCriterion(PropertyCriterion.eq(productItemTypeCb.proto().type(), ProductItemType.Type.service));
                                productItemTypeCb.setValue(null);
                                serviceTypesCb.setVisible(true);
                                featureTypeCb.setVisible(false);
                            }else {
                                productItemTypeCb.addCriterion(PropertyCriterion.eq(productItemTypeCb.proto().type(), ProductItemType.Type.feature));
                                productItemTypeCb.setValue(null);
                                serviceTypesCb.setVisible(false);
                                featureTypeCb.setVisible(true);
                            }
                        }
                    });
                    content.setWidget(++row, 0, wd);
                    
                    
                    wd = new DecoratorBuilder(inject(proto().productItemType().serviceType())).build();
                    serviceTypesCb = wd.getComnponent();
                    ((CComboBox<Service.Type>)serviceTypesCb).addValueChangeHandler(new ValueChangeHandler<Service.Type>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<Service.Type> event) {
                            productItemTypeCb.resetCriteria();
                            productItemTypeCb.addCriterion(PropertyCriterion.eq(productItemTypeCb.proto().type(), ((CComboBox<ProductItemType.Type>)itemTypeCb).getValue()));
                            
                            if (event.getValue() != null) {
                                productItemTypeCb.addCriterion(PropertyCriterion.eq(productItemTypeCb.proto().serviceType(), event.getValue()));
                            }
                            
                            productItemTypeCb.setValue(null);
                        }
                    });
                    serviceTypesCb.setVisible(false);
                    content.setWidget(++row, 0, wd); 
                    
                    wd = new DecoratorBuilder(inject(proto().productItemType().featureType())).build();
                    featureTypeCb = wd.getComnponent();
                    ((CComboBox<Feature.Type>)featureTypeCb).addValueChangeHandler(new ValueChangeHandler<Feature.Type>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<Feature.Type> event) {
                            productItemTypeCb.resetCriteria();
                            productItemTypeCb.addCriterion(PropertyCriterion.eq(productItemTypeCb.proto().type(), ((CComboBox<ProductItemType.Type>)itemTypeCb).getValue()));
                            
                            if (event.getValue() != null) {
                                productItemTypeCb.addCriterion(PropertyCriterion.eq(productItemTypeCb.proto().featureType(), event.getValue()));
                            }
                            
                            productItemTypeCb.setValue(null);
                        }
                    });
                    featureTypeCb.setVisible(false);
                    content.setWidget(++row, 0, wd); 
                    
                    productItemTypeCb = (CEntityComboBox<ProductItemType> )inject(proto().productItemType());
                    wd = new DecoratorBuilder(productItemTypeCb).build();
                    productItemTypeCb.addValueChangeHandler(new ValueChangeHandler<ProductItemType>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<ProductItemType> event) {
                            ProductItemType pt = event.getValue();
                            
                            if (pt != null) {
                                if (pt.type().getValue() == ProductItemType.Type.service) {
                                    ((CComboBox<Service.Type>)serviceTypesCb).setValue(pt.serviceType().getValue());
                                } else{
                                    ((CComboBox<Feature.Type>)featureTypeCb).setValue(pt.featureType().getValue());
                                }
                                
                                ((CComboBox<ProductItemType.Type>)itemTypeCb).setValue(pt.type().getValue());
                            }
                        }
                    });
                    
                    content.setWidget(++row, 0, wd);
                    
                    wd = new DecoratorBuilder(inject(proto().chargeCode())).build();
                    chargeCodeCb = wd.getComnponent();
                    content.setWidget(++row, 0, wd);
                }

    
  
                return content;
            }
        }

    }

}
