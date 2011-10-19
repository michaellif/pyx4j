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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderItemDecorator;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceConcession;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class ServiceEditorForm extends CrmEntityForm<Service> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public ServiceEditorForm(IFormView<Service> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public ServiceEditorForm(IEditableComponentFactory factory, IFormView<Service> parentView) {
        super(Service.class, factory);
        setParentView(parentView);
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

        main.add(new CrmSectionSeparator(i18n.tr("Items:")));
        main.add(inject(proto().items(), createItemsFolderEditor()));

        return new CrmScrollPanel(main);
    }

    public IsWidget createEligibilityTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new CrmSectionSeparator(i18n.tr("Features:")));
        main.add(inject(proto().features(), createFeaturesFolderEditor()));

        main.add(new CrmSectionSeparator(i18n.tr("Concessions:")));
        main.add(inject(proto().concessions(), createConcessionsFolderEditor()));

        return new CrmScrollPanel(main);
    }

    private CEntityFolder<ServiceItem> createItemsFolderEditor() {
        return new VistaTableFolder<ServiceItem>(ServiceItem.class, i18n.tr("Item"), isEditable()) {
            private final VistaTableFolder<ServiceItem> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "20em"));
                columns.add(new EntityFolderColumnDescriptor(proto().price(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
                columns.add(new EntityFolderColumnDescriptor(proto().element(), "15em"));
                return columns;
            }

            @Override
            protected CEntityFolderItem<ServiceItem> createItem(boolean first) {
                return new CEntityFolderRowEditor<ServiceItem>(ServiceItem.class, columns()) {
//                    private CEntityComboBox<AptUnit> combo;

                    @Override
                    public IFolderItemDecorator<ServiceItem> createDecorator() {
                        return new VistaTableFolderItemDecorator<ServiceItem>(parent);
                    }

                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        Class<? extends IEntity> buildingElementClass = null;
                        switch (ServiceEditorForm.this.getValue().type().getValue()) {
                        case residentialUnit:
                        case residentialShortTermUnit:
                        case commercialUnit:
                            buildingElementClass = AptUnit.class;
                            break;
                        case garage:
                            buildingElementClass = Parking.class;
                            break;
                        case storage:
                            buildingElementClass = LockerArea.class;
                            break;
                        case roof:
                            buildingElementClass = Roof.class;
                            break;
                        }

                        CComponent<?> comp;
                        if (column.getObject() == proto().element()) {
                            if (parent.isEditable()) {
                                comp = inject(column.getObject(), new CEntityComboBox(buildingElementClass));
                                CEntityComboBox<BuildingElement> combo = (CEntityComboBox) comp;

                                Service value = ServiceEditorForm.this.getValue();
                                combo.addCriterion(PropertyCriterion.eq(combo.proto().belongsTo(), value.catalog().belongsTo().detach()));
                            } else {
                                comp = inject(column.getObject(), new CEntityCrudHyperlink<AptUnit>(MainActivityMapper.getCrudAppPlace(buildingElementClass)));
                            }
                        } else {
                            comp = super.createCell(column);
                        }

                        if (column.getObject() == proto().type()) {
                            if (comp instanceof CEntityComboBox<?>) {
                                CEntityComboBox<ServiceItemType> combo = (CEntityComboBox<ServiceItemType>) comp;
                                combo.addCriterion(PropertyCriterion.eq(combo.proto().serviceType(), ServiceEditorForm.this.getValue().type().getValue()));
                            }
                        }

                        return comp;
                    }
                };
            }
        };
    }

    private CEntityFolder<ServiceFeature> createFeaturesFolderEditor() {
        return new VistaTableFolder<ServiceFeature>(ServiceFeature.class, i18n.tr("Feature"), isEditable()) {
            private final VistaTableFolder<ServiceFeature> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().feature(), "50em"));
                return columns;
            }

            @Override
            protected void addItem() {
                new ShowPopUpBox<SelectFeatureBox>(new SelectFeatureBox()) {
                    @Override
                    protected void onClose(SelectFeatureBox box) {
                        if (box.getSelectedFeatures() != null) {
                            for (Feature item : box.getSelectedFeatures()) {
                                ServiceFeature newItem = EntityFactory.create(ServiceFeature.class);
                                newItem.feature().set(item);
                                addItem(newItem);
                            }
                        }
                    }
                };
            }

            @Override
            protected IFolderDecorator<ServiceFeature> createDecorator() {
                VistaTableFolderDecorator<ServiceFeature> decor = new VistaTableFolderDecorator<ServiceFeature>(columns(), parent);
                decor.setShowHeader(false);
                return decor;
            }

            @Override
            protected CEntityFolderItem<ServiceFeature> createItem(boolean first) {
                return new CEntityFolderRowEditor<ServiceFeature>(ServiceFeature.class, columns()) {
                    @Override
                    public IFolderItemDecorator<ServiceFeature> createDecorator() {
                        return new VistaTableFolderItemDecorator<ServiceFeature>(parent);
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        if (column.getObject() == proto().feature()) {
                            return inject(column.getObject(), new CEntityLabel());
                        }
                        return super.createCell(column);
                    }
                };
            }
        };
    }

    private CEntityFolder<ServiceConcession> createConcessionsFolderEditor() {
        return new VistaTableFolder<ServiceConcession>(ServiceConcession.class, i18n.tr("Concession"), isEditable()) {
            private final VistaTableFolder<ServiceConcession> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().concession(), "50em"));
                return columns;
            }

            @Override
            protected void addItem() {
                new ShowPopUpBox<SelectConcessionBox>(new SelectConcessionBox()) {
                    @Override
                    protected void onClose(SelectConcessionBox box) {
                        if (box.getSelectedConcessions() != null) {
                            for (Concession item : box.getSelectedConcessions()) {
                                ServiceConcession newItem = EntityFactory.create(ServiceConcession.class);
                                newItem.concession().set(item);
                                addItem(newItem);
                            }
                        }
                    }
                };
            }

            @Override
            protected IFolderDecorator<ServiceConcession> createDecorator() {
                VistaTableFolderDecorator<ServiceConcession> decor = new VistaTableFolderDecorator<ServiceConcession>(columns(), parent);
                decor.setShowHeader(false);
                return decor;
            }

            @Override
            protected CEntityFolderItem<ServiceConcession> createItem(boolean first) {
                return new CEntityFolderRowEditor<ServiceConcession>(ServiceConcession.class, columns()) {
                    @Override
                    public IFolderItemDecorator<ServiceConcession> createDecorator() {
                        return new VistaTableFolderItemDecorator<ServiceConcession>(parent);
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        if (column.getObject() == proto().concession()) {
                            return inject(column.getObject(), new CEntityLabel());
                        }
                        return super.createCell(column);
                    }
                };
            }
        };
    }

    private class SelectFeatureBox extends OkCancelBox {

        private List<Feature> selectedFeatures;

        public SelectFeatureBox() {
            super(i18n.tr("Select Features"));
            ((ServiceEditorView) getParentView()).getFeatureListerView().getLister().releaseSelection();
        }

        @Override
        protected Widget createContent() {
            okButton.setEnabled(false);
            ((ServiceEditorView) getParentView()).getFeatureListerView().getLister().addItemSelectionHandler(new ItemSelectionHandler<Feature>() {
                @Override
                public void onSelect(Feature selectedItem) {
                    okButton.setEnabled(!((ServiceEditorView) getParentView()).getFeatureListerView().getLister().getSelectedItems().isEmpty());
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(((ServiceEditorView) getParentView()).getFeatureListerView().asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        protected void setSize() {
            setSize("700px", "200px");
        }

        @Override
        protected boolean onOk() {
            selectedFeatures = ((ServiceEditorView) getParentView()).getFeatureListerView().getLister().getSelectedItems();
            return true;
        }

        @Override
        protected void onCancel() {
            selectedFeatures = null;
        }

        protected List<Feature> getSelectedFeatures() {
            return selectedFeatures;
        }
    }

    private class SelectConcessionBox extends OkCancelBox {

        private List<Concession> selectedConcessions;

        public SelectConcessionBox() {
            super(i18n.tr("Select Concessions"));
            ((ServiceEditorView) getParentView()).getConcessionListerView().getLister().releaseSelection();
        }

        @Override
        protected Widget createContent() {
            okButton.setEnabled(false);
            ((ServiceEditorView) getParentView()).getConcessionListerView().getLister().addItemSelectionHandler(new ItemSelectionHandler<Concession>() {
                @Override
                public void onSelect(Concession selectedItem) {
                    okButton.setEnabled(!((ServiceEditorView) getParentView()).getConcessionListerView().getLister().getSelectedItems().isEmpty());
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(((ServiceEditorView) getParentView()).getConcessionListerView().asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        protected void setSize() {
            setSize("700px", "200px");
        }

        @Override
        protected boolean onOk() {
            selectedConcessions = ((ServiceEditorView) getParentView()).getConcessionListerView().getLister().getSelectedItems();
            return true;
        }

        @Override
        protected void onCancel() {
            selectedConcessions = null;
        }

        protected List<Concession> getSelectedConcessions() {
            return selectedConcessions;
        }
    }
}