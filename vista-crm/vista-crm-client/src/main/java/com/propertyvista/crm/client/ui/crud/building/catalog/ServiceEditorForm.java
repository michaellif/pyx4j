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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.site.client.ui.crud.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.ListerBase.ItemSelectionHandler;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.CrmTableFolderItemDecorator;
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

    private CEntityFolderEditor<ServiceItem> createItemsFolderEditor() {
        return new CrmEntityFolder<ServiceItem>(ServiceItem.class, i18n.tr("Item"), isEditable()) {
            private final CrmEntityFolder<ServiceItem> parent = this;

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
            protected CEntityFolderItemEditor<ServiceItem> createItem() {
                return new CEntityFolderRowEditor<ServiceItem>(ServiceItem.class, columns()) {
                    @Override
                    public IFolderItemEditorDecorator<ServiceItem> createFolderItemDecorator() {
                        return new CrmTableFolderItemDecorator<ServiceItem>(parent);
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        CComponent<?> comp;

                        if (column.getObject() == proto().element()) {
                            if (parent.isEditable()) {
                                comp = inject(column.getObject(), new CEntityComboBox<AptUnit>(AptUnit.class));
//                                CEntityComboBox<AptUnit> combo = (CEntityComboBox<AptUnit>) comp;
//                                combo.setOptionsFilter(new OptionsFilter<AptUnit>() {
//                                    @Override
//                                    public boolean acceptOption(AptUnit entity) {
//                                        Service value = ServiceEditorForm.this.getValue();
//                                        if (value != null && !value.isNull()) {
//                                            return entity.belongsTo().equals(value.catalog().belongsTo());
//                                        }
//                                        return false;
//                                    }
//                                });

                                @SuppressWarnings("unchecked")
                                CEntityComboBox<AptUnit> combo = (CEntityComboBox<AptUnit>) comp;
                                combo.addCriterion(PropertyCriterion.eq(combo.proto().belongsTo(), ServiceEditorForm.this.getValue().catalog().belongsTo()));
                            } else {
                                comp = inject(column.getObject(), new CEntityCrudHyperlink<AptUnit>(MainActivityMapper.getCrudAppPlace(AptUnit.class)));
                            }
                        } else {
                            comp = super.createCell(column);
                        }

                        if (column.getObject() == proto().type()) {
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

    private CEntityFolderEditor<ServiceFeature> createFeaturesFolderEditor() {
        return new CrmEntityFolder<ServiceFeature>(ServiceFeature.class, i18n.tr("Feature"), isEditable()) {
            private final CrmEntityFolder<ServiceFeature> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().feature(), "50em"));
                return columns;
            }

            @Override
            protected IFolderEditorDecorator<ServiceFeature> createFolderDecorator() {
                CrmTableFolderDecorator<ServiceFeature> decor = new CrmTableFolderDecorator<ServiceFeature>(columns(), parent);
                setExternalAddItemProcessing(true);
                decor.addItemAddClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
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
                });
                decor.setShowHeader(false);
                return decor;
            }

            @Override
            protected CEntityFolderItemEditor<ServiceFeature> createItem() {
                return new CEntityFolderRowEditor<ServiceFeature>(ServiceFeature.class, columns()) {
                    @Override
                    public IFolderItemEditorDecorator<ServiceFeature> createFolderItemDecorator() {
                        return new CrmTableFolderItemDecorator<ServiceFeature>(parent);
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

    private CEntityFolderEditor<ServiceConcession> createConcessionsFolderEditor() {
        return new CrmEntityFolder<ServiceConcession>(ServiceConcession.class, i18n.tr("Concession"), isEditable()) {
            private final CrmEntityFolder<ServiceConcession> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().concession(), "50em"));
                return columns;
            }

            @Override
            protected IFolderEditorDecorator<ServiceConcession> createFolderDecorator() {
                CrmTableFolderDecorator<ServiceConcession> decor = new CrmTableFolderDecorator<ServiceConcession>(columns(), parent);
                setExternalAddItemProcessing(true);
                decor.addItemAddClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
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
                });
                decor.setShowHeader(false);
                return decor;
            }

            @Override
            protected CEntityFolderItemEditor<ServiceConcession> createItem() {
                return new CEntityFolderRowEditor<ServiceConcession>(ServiceConcession.class, columns()) {
                    @Override
                    public IFolderItemEditorDecorator<ServiceConcession> createFolderItemDecorator() {
                        return new CrmTableFolderItemDecorator<ServiceConcession>(parent);
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
            super("Select Features");
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
            super("Select Concessions");
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