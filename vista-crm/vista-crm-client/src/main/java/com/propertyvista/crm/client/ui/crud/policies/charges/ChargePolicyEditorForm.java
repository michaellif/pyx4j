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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.domain.financial.offering.ChargeCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.policy.dto.ChargePolicyDTO;
import com.propertyvista.domain.policy.policies.domain.ProductTaxPolicyItem;

public class ChargePolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<ChargePolicyDTO> {
    private final static I18n i18n = I18n.get(ChargePolicyEditorForm.class);

    private boolean viewMode;

    public ChargePolicyEditorForm() {
        this(false);

        viewMode = false;
    }

    public ChargePolicyEditorForm(boolean viewMode) {
        super(ChargePolicyDTO.class, viewMode);

        this.viewMode = viewMode;
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
        
        panel.setWidget(++row, 0, inject(proto().policyItems(), new ChargePolicyEditorFolder(viewMode)));

        return panel;
    }
    
    
    private static class ChargePolicyEditorFolder extends VistaBoxFolder<ProductTaxPolicyItem> {

        private final boolean viewMode;
        
        public ChargePolicyEditorFolder(boolean viewMode) {
            super(ProductTaxPolicyItem.class);
            
            this.viewMode = viewMode;
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof ProductTaxPolicyItem) {
                return new ChargePolicyItemEditor(viewMode);
            } else {
                return super.create(member);
            }
        }

        private static class ChargePolicyItemEditor extends CEntityDecoratableEditor<ProductTaxPolicyItem> {
            
            private final static I18n i18n = I18n.get(ChargePolicyItemEditor.class);
            
            //private CComponent<?,?> itemTypeCb;
            //private CComponent<?,?> serviceTypesCb;
            //private CComponent<?,?> featureTypeCb;
            //private CEntityComboBox<ProductItemType> productItemTypeCb;
            //private CComponent<?,?> chargeCodeCb;
            
            private Widget serviceType;

            private Widget featureType;
            
            private final boolean viewMode;
            
            
            public ChargePolicyItemEditor(boolean viewMode) {
                super(ProductTaxPolicyItem.class);
                
                this.viewMode = viewMode;
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                int row = -1;
                
                if (!viewMode) {               
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType().type())).build());
                    get(proto().productItemType().type()).addValueChangeHandler(new ValueChangeHandler<ProductItemType.Type>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<ProductItemType.Type> event) {
                            ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).resetCriteria();
                            
                            if (event.getValue() == ProductItemType.Type.service)
                            {
                                ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).
                                    addCriterion(PropertyCriterion.eq(((CEntityComboBox<ProductItemType>)get(proto().productItemType())).proto().type(), ProductItemType.Type.service));
                                ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).setValue(null);
                                get(proto().productItemType().serviceType()).setVisible(true);
                                get(proto().productItemType().featureType()).setVisible(false);
                            }else {
                                ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).
                                    addCriterion(PropertyCriterion.eq(((CEntityComboBox<ProductItemType>)get(proto().productItemType())).proto().type(), ProductItemType.Type.feature));
                                ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).setValue(null);
                                get(proto().productItemType().serviceType()).setVisible(false);
                                get(proto().productItemType().featureType()).setVisible(true);
                            }
                        }
                    });
                    
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType().serviceType())).build());
                    get(proto().productItemType().serviceType()).addValueChangeHandler(new ValueChangeHandler<Service.Type>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<Service.Type> event) {
                            ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).resetCriteria();
                            ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).
                                addCriterion(PropertyCriterion.eq(((CEntityComboBox<ProductItemType>)get(proto().productItemType())).proto().type(), get(proto().productItemType().type()).getValue()));
                            
                            if (event.getValue() != null) {
                                ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).
                                    addCriterion(PropertyCriterion.eq(((CEntityComboBox<ProductItemType>)get(proto().productItemType())).proto().serviceType(), event.getValue()));
                            }
                            
                            ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).setValue(null);
                        }
                    });
                    get(proto().productItemType().serviceType()).setVisible(false);
                    
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType().featureType())).build());
                    get(proto().productItemType().featureType()).addValueChangeHandler(new ValueChangeHandler<Feature.Type>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<Feature.Type> event) {
                            ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).resetCriteria();
                            ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).
                                addCriterion(PropertyCriterion.eq(((CEntityComboBox<ProductItemType>)get(proto().productItemType())).proto().type(), get(proto().productItemType().type()).getValue()));
                            
                            if (event.getValue() != null) {
                                ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).
                                    addCriterion(PropertyCriterion.eq(((CEntityComboBox<ProductItemType>)get(proto().productItemType())).proto().featureType(), event.getValue()));
                            }
                            
                            ((CEntityComboBox<ProductItemType>)get(proto().productItemType())).setValue(null);
                        }
                    });
                    get(proto().productItemType().featureType()).setVisible(false);
                    
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType())).build());
                    get(proto().productItemType()).addValueChangeHandler(new ValueChangeHandler<ProductItemType>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<ProductItemType> event) {
                            ProductItemType pt = event.getValue();
                            
                            if (pt != null) {
                                if (pt.type().getValue() == ProductItemType.Type.service) {
                                    get(proto().productItemType().serviceType()).setValue(pt.serviceType().getValue());
                                } else{
                                    get(proto().productItemType().featureType()).setValue(pt.featureType().getValue());
                                }
                                
                                get(proto().productItemType().type()).setValue(pt.type().getValue());
                            }
                        }
                    });
                    
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().chargeCode())).build());             
                    get(proto().chargeCode()).addValueChangeHandler(new ValueChangeHandler<ChargeCode>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<ChargeCode> event) {
                            //proto().productItemType().chargeCode().set(event.getValue());
                            
                        }
                    });
                    
                }
                else {
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType().name())).build());
                    content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productItemType().type())).build());
                    
                    content.setWidget(++row, 0, serviceType = new DecoratorBuilder(inject(proto().productItemType().serviceType())).build());
                    content.setWidget(++row, 0, featureType = new DecoratorBuilder(inject(proto().productItemType().featureType())).build());
                    
                    WidgetDecorator wd = new DecoratorBuilder(inject(proto().chargeCode().name())).build();
                    wd.getLabel().setText(i18n.tr("Charge Code"));
                    content.setWidget(++row, 0, wd);
                }

                return content;
            }
            
            @Override
            protected void onPopulate() {
                super.onPopulate();
                if (viewMode)
                {
                    serviceType.setVisible(false);
                    featureType.setVisible(false);
    
                    switch (getValue().productItemType().type().getValue()) {
                    case service:
                        serviceType.setVisible(true);
                        break;
    
                    case feature:
                        featureType.setVisible(true);
                        break;
                    }
                }
            }
        }

    }

}
