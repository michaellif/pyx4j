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

import com.google.gwt.dom.client.Style.Unit;
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

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmFolderItemDecorator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmHeader2Decorator;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ServiceEditorForm extends CrmEntityForm<Service> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public ServiceEditorForm() {
        super(Service.class, new CrmEditorsComponentFactory());
    }

    public ServiceEditorForm(IEditableComponentFactory factory) {
        super(Service.class, factory);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createEligibilityTab(), i18n.tr("Eligibility"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;

    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    public IsWidget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        main.add(split);
        split.getLeftPanel().add(inject(proto().type(), new CLabel()), 10);
        split.getLeftPanel().add(inject(proto().name()), 10);

        split.getRightPanel().add(inject(proto().depositType()), 15);

        main.add(inject(proto().description()), 50);

        main.add(new CrmHeader2Decorator(i18n.tr("Items:")));
        main.add(inject(proto().items(), createItemsFolderEditor()));

        return new CrmScrollPanel(main);
    }

    public IsWidget createEligibilityTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new CrmHeader2Decorator(i18n.tr("Features:")));
        main.add(inject(proto().features(), createFeaturesFolderEditor()));

        main.add(new CrmHeader2Decorator(i18n.tr("Concessions:")));
        main.add(inject(proto().concessions(), createConcessionsFolderEditor()));

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
                                        Service value = ServiceEditorForm.this.getValue();
                                        if (value != null && !value.isNull()) {
                                            return entity.serviceType().equals(value.type());
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

    private CEntityFolderEditor<Feature> createFeaturesFolderEditor() {
        return new CrmEntityFolder<Feature>(Feature.class, i18n.tr("Feature"), isEditable()) {
            private final CrmEntityFolder<Feature> thisRef = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto(), "50em"));
                return columns;
            }

            @Override
            protected CEntityFolderItemEditor<Feature> createItem() {
                return new CEntityFolderRowEditor<Feature>(Feature.class, columns()) {
                    @Override
                    public IFolderItemEditorDecorator<Feature> createFolderItemDecorator() {
                        return new CrmFolderItemDecorator<Feature>(thisRef);
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        CComponent<?> comp = super.createCell(column);
                        if (column.getObject() == proto()) {
                            if (comp instanceof CEntityComboBox<?>) {
                                @SuppressWarnings("unchecked")
                                CEntityComboBox<Feature> combo = (CEntityComboBox<Feature>) comp;
                                combo.setOptionsFilter(new OptionsFilter<Feature>() {
                                    @Override
                                    public boolean acceptOption(Feature entity) {
                                        Service value = ServiceEditorForm.this.getValue();
                                        if (value != null && !value.isNull()) {
                                            return entity.catalog().equals(value.catalog());
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

    private CEntityFolderEditor<Concession> createConcessionsFolderEditor() {
        return new CrmEntityFolder<Concession>(Concession.class, i18n.tr("Concession"), isEditable()) {
            private final CrmEntityFolder<Concession> thisRef = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto(), "50em"));
                return columns;
            }

            @Override
            protected CEntityFolderItemEditor<Concession> createItem() {
                return new CEntityFolderRowEditor<Concession>(Concession.class, columns()) {
                    @Override
                    public IFolderItemEditorDecorator<Concession> createFolderItemDecorator() {
                        return new CrmFolderItemDecorator<Concession>(thisRef);
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        CComponent<?> comp = super.createCell(column);
                        if (column.getObject() == proto()) {
                            if (comp instanceof CEntityComboBox<?>) {
                                @SuppressWarnings("unchecked")
                                CEntityComboBox<Concession> combo = (CEntityComboBox<Concession>) comp;
                                combo.setOptionsFilter(new OptionsFilter<Concession>() {
                                    @Override
                                    public boolean acceptOption(Concession entity) {
                                        Service value = ServiceEditorForm.this.getValue();
                                        if (value != null && !value.isNull()) {
                                            return entity.catalog().equals(value.catalog());
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