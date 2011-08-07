/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmFolderItemDecorator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmHeader2Decorator;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class FeatureEditorForm extends CrmEntityForm<Feature> {

    public FeatureEditorForm() {
        super(Feature.class, new CrmEditorsComponentFactory());
    }

    public FeatureEditorForm(IEditableComponentFactory factory) {
        super(Feature.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());

        main.add(split);
        split.getLeftPanel().add(inject(proto().type(), new CLabel()), 10);
        split.getLeftPanel().add(inject(proto().name()), 10);
        split.getLeftPanel().add(inject(proto().isMandatory()), 4);

        split.getRightPanel().add(inject(proto().priceType()), 18);
        split.getRightPanel().add(inject(proto().depositType()), 15);
        split.getRightPanel().add(inject(proto().isRecurring()), 4);

        main.add(inject(proto().description()), 50);

        main.add(new CrmHeader2Decorator(i18n.tr("Items:")));
        main.add(inject(proto().items(), createItemsFolderEditor()));

        return new CrmScrollPanel(main);
    }

    private CEntityFolderEditor<ServiceItem> createItemsFolderEditor() {
        return new CrmEntityFolder<ServiceItem>(ServiceItem.class, i18n.tr("Item"), isEditable()) {
            private final CrmEntityFolder<ServiceItem> thisRef = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().itemType(), "20em"));
                columns.add(new EntityFolderColumnDescriptor(proto().price(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
//                columns.add(new EntityFolderColumnDescriptor(proto().element(), "15em"));
                return columns;
            }

            @Override
            protected CEntityFolderItemEditor<ServiceItem> createItem() {
                return new CEntityFolderRowEditor<ServiceItem>(ServiceItem.class, columns()) {
                    @Override
                    public IFolderItemEditorDecorator<ServiceItem> createFolderItemDecorator() {
                        return new CrmFolderItemDecorator<ServiceItem>(thisRef);
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        CComponent<?> comp = super.createCell(column);
                        if (column.getObject() == proto().itemType()) {
                            if (comp instanceof CEntityComboBox<?>) {
                                @SuppressWarnings("unchecked")
                                CEntityComboBox<ServiceItemType> combo = (CEntityComboBox<ServiceItemType>) comp;
                                combo.setOptionsFilter(new OptionsFilter<ServiceItemType>() {
                                    @Override
                                    public boolean acceptOption(ServiceItemType entity) {
                                        Feature value = FeatureEditorForm.this.getValue();
                                        if (value != null && !value.isNull()) {
                                            return entity.featureType().equals(value.type());
                                        }
                                        return false;
                                    }
                                });
                            }
                        }
                        return comp;
                    }
                };
            }
        };
    }
}