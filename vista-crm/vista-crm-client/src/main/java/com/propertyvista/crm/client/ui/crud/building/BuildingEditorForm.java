/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.validators.FutureDateValidation;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmBoxFolderDecorator;
import com.propertyvista.crm.client.ui.components.CrmBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityFolder;
import com.propertyvista.crm.client.ui.components.CrmTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.CrmTableFolderItemDecorator;
import com.propertyvista.crm.client.ui.components.OkCancelBox;
import com.propertyvista.crm.client.ui.components.ShowPopUpBox;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.crm.client.ui.components.media.CrmMediaListFolderEditor;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.company.OrganisationContact;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class BuildingEditorForm extends CrmEntityForm<BuildingDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public BuildingEditorForm(IFormView<BuildingDTO> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public BuildingEditorForm(IEditableComponentFactory factory, IFormView<BuildingDTO> parentView) {
        super(BuildingDTO.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        // TODO - add this data processing later! :
        //  main.add(inject(proto().media()), 15);

        tabPanel.addDisable(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getDashboardView().asWidget(), i18n.tr("Dashboard"));

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createDetailsTab(), i18n.tr("Details"));

        tabPanel.addDisable(isEditable() ? new HTML() : new ScrollPanel(((BuildingViewerView) getParentView()).getFloorplanListerView().asWidget()),
                i18n.tr("Floorplans"));
        tabPanel.addDisable(isEditable() ? new HTML() : new ScrollPanel(((BuildingViewerView) getParentView()).getUnitListerView().asWidget()),
                i18n.tr("Units"));

        FlowPanel combinedtab = new FlowPanel();
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Elevators:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getElevatorListerView().asWidget());
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Boilers:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getBoilerListerView().asWidget());
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Roofs:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getRoofListerView().asWidget());
        tabPanel.addDisable(new ScrollPanel(combinedtab), i18n.tr("Mechanicals"));

        combinedtab = new FlowPanel();
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Parkings:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getParkingListerView().asWidget());
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Locker Areas:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getLockerAreaListerView().asWidget());
        tabPanel.addDisable(new ScrollPanel(combinedtab), i18n.tr("Add-ons"));

        tabPanel.add(createFinancialTab(), i18n.tr("Financial"));
        tabPanel.add(createMarketingTab(), i18n.tr("Marketing"));

        combinedtab = new FlowPanel();
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Services:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getServiceListerView().asWidget());
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Features:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getFeatureListerView().asWidget());
        combinedtab.add(new CrmSectionSeparator(i18n.tr("Concessions:")));
        combinedtab.add(isEditable() ? new HTML() : ((BuildingViewerView) getParentView()).getConcessionListerView().asWidget());
        tabPanel.addDisable(new ScrollPanel(combinedtab), i18n.tr("Service Catalog"));

        tabPanel.add(createContactTab(), i18n.tr("Contacts"));
        tabPanel.addDisable(new CrmScrollPanel(new Label("Notes and attachments goes here... ")), i18n.tr("Notes & Attachments"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void addValidations() {
        new FutureDateValidation(get(proto().info().structureBuildYear()));
        new FutureDateValidation(get(proto().financial().dateAquired()));
        new FutureDateValidation(get(proto().financial().lastAppraisalDate()));

        get(proto().complex()).addValueChangeHandler(new ValueChangeHandler<Complex>() {

            @Override
            public void onValueChange(ValueChangeEvent<Complex> event) {
                get(proto().complexPrimary()).setEditable(!get(proto().complex()).isValueEmpty());
            }
        });
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    private Widget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());
        main.add(split);

        split.getLeftPanel().add(inject(proto().propertyCode()), 12);
        split.getLeftPanel().add(inject(proto().info().name()), 15);
        split.getLeftPanel().add(inject(proto().info().type()), 12);
        split.getLeftPanel().add(inject(proto().propertyManager()), 15);

        split.getRightPanel().add(inject(proto().info().shape()), 7);
        split.getRightPanel().add(inject(proto().info().totalStoreys()), 5);
        split.getRightPanel().add(inject(proto().info().residentialStoreys()), 5);
        split.getRightPanel().add(inject(proto().complex()), 15);
        split.getRightPanel().add(inject(proto().complexPrimary()), 15);

        main.add(new VistaLineSeparator());

        main.add(split = new VistaDecoratorsSplitFlowPanel(!isEditable()));
        AddressUtils.injectIAddress(split, proto().info().address(), this, false);

        main.add(new HTML("&nbsp"));

        main.add(inject(proto().geoLocation()), 28);

        main.add(new HTML("&nbsp"));

        main.add(new CrmSectionSeparator(i18n.tr("Amenities:")));
        main.add(inject(proto().amenities(), createAmenitiesListEditor()));

        return new CrmScrollPanel(main);
    }

    private Widget createDetailsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());
        main.add(split);

        split.getLeftPanel().add(inject(proto().info().structureType()), 15);
        split.getLeftPanel().add(inject(proto().info().structureBuildYear()), 10);
        split.getLeftPanel().add(inject(proto().info().constructionType()), 15);
        split.getLeftPanel().add(inject(proto().info().foundationType()), 15);
        split.getLeftPanel().add(inject(proto().info().floorType()), 15);

        split.getRightPanel().add(inject(proto().info().landArea()), 15);
        split.getRightPanel().add(inject(proto().info().waterSupply()), 15);
        split.getRightPanel().add(inject(proto().info().centralAir()), 15);
        split.getRightPanel().add(inject(proto().info().centralHeat()), 15);

        main.add(new VistaLineSeparator());
        if (isEditable()) {
            main.add(inject(proto().contacts().website()), 50);
        } else {
            main.add(inject(proto().contacts().website(), new CHyperlink(new Command() {
                @Override
                public void execute() {
                    Window.open(getValue().contacts().website().getValue(), proto().contacts().website().getMeta().getCaption(), null);
                }
            })), 50);
        }

        return new CrmScrollPanel(main);
    }

    private Widget createFinancialTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());
        main.add(split);

        split.getLeftPanel().add(inject(proto().financial().dateAquired()), 8.2);
        split.getLeftPanel().add(inject(proto().financial().purchasePrice()), 10);
        split.getLeftPanel().add(inject(proto().financial().marketPrice()), 10);

        split.getRightPanel().add(inject(proto().financial().lastAppraisalDate()), 8.2);
        split.getRightPanel().add(inject(proto().financial().lastAppraisalValue()), 10);
        split.getRightPanel().add(inject(proto().financial().currency().name()), split.getRightPanel().getDefaultLabelWidth(), 10, i18n.tr("Currency Name"));

        main.add(new CrmSectionSeparator(i18n.tr("Included Utilities:")));
        main.add(inject(proto().serviceCatalog().includedUtilities(), createUtilitiesListEditor()));

        return new CrmScrollPanel(main);
    }

    private Widget createMarketingTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        SubtypeInjectors.injectMarketing(main, proto().marketing(), this);

        main.add(new VistaLineSeparator());

        main.add(inject(proto().contacts().email().address()), main.getDefaultLabelWidth(), 30, i18n.tr("Email Address"));
        SubtypeInjectors.injectPhones(main, proto().contacts().phones2Migrate(), this, false, true);

        main.add(new CrmSectionSeparator(i18n.tr("Media:")));
        main.add(inject(proto().media(), new CrmMediaListFolderEditor(isEditable(), ImageTarget.Building)));

        return new CrmScrollPanel(main);
    }

    private Widget createContactTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().contacts().contacts(), createContactsListEditor()));

        return new CrmScrollPanel(main);
    }

//
// List Viewers:
    private CEntityFolderEditor<BuildingAmenity> createAmenitiesListEditor() {
        return new CrmEntityFolder<BuildingAmenity>(BuildingAmenity.class, i18n.tr("Amenity"), isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
                return columns;
            }
        };
    }

    private CEntityFolderEditor<ServiceItemType> createUtilitiesListEditor() {
        return new CrmEntityFolder<ServiceItemType>(ServiceItemType.class, i18n.tr("Utility"), isEditable()) {
            private final CrmEntityFolder<ServiceItemType> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "30"));
                return columns;
            }

            @Override
            protected IFolderEditorDecorator<ServiceItemType> createFolderDecorator() {
                CrmTableFolderDecorator<ServiceItemType> decor = new CrmTableFolderDecorator<ServiceItemType>(columns(), parent);
                setExternalAddItemProcessing(true);
                decor.addItemAddClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        new ShowPopUpBox<SelectUtilityBox>(new SelectUtilityBox()) {
                            @Override
                            protected void onClose(SelectUtilityBox box) {
                                if (box.getSelectedItems() != null) {
                                    for (ServiceItemType item : box.getSelectedItems()) {
                                        addItem(item);
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
            protected CEntityFolderItemEditor<ServiceItemType> createItem() {
                return new CEntityFolderRowEditor<ServiceItemType>(ServiceItemType.class, columns()) {
                    @Override
                    public IFolderItemEditorDecorator<ServiceItemType> createFolderItemDecorator() {
                        return new CrmTableFolderItemDecorator<ServiceItemType>(parent);
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        if (column.getObject() == proto().name()) {
                            return inject(column.getObject(), new CLabel());
                        }
                        return super.createCell(column);
                    }
                };
            }
        };
    }

    private CEntityFolderEditor<OrganisationContact> createContactsListEditor() {
        return new CrmEntityFolder<OrganisationContact>(OrganisationContact.class, i18n.tr("Contact"), isEditable()) {
            private final CrmEntityFolder<OrganisationContact> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                return null;
            }

            @Override
            protected IFolderEditorDecorator<OrganisationContact> createFolderDecorator() {
                return new CrmBoxFolderDecorator<OrganisationContact>(parent);
            }

            @Override
            protected CEntityFolderItemEditor<OrganisationContact> createItem() {
                return new CEntityFolderRowEditor<OrganisationContact>(OrganisationContact.class, columns()) {

                    @Override
                    public IFolderItemEditorDecorator<OrganisationContact> createFolderItemDecorator() {
                        return new CrmBoxFolderItemDecorator<OrganisationContact>(parent);
                    }

                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!parent.isEditable());

                        main.add(inject(proto().description()), 35);
                        if (parent.isEditable()) {
                            main.add(inject(proto().person()), 35);
                        } else {
                            main.add(inject(proto().person()), 35);
                            main.add(inject(proto().person().workPhone()), 10);
                            main.add(inject(proto().person().mobilePhone()), 10);
                            main.add(inject(proto().person().homePhone()), 10);
                            main.add(inject(proto().person().email()), 20);
                        }

                        return main;
                    }
                };
            }
        };
    }

//
// Selection Boxes:

    private class SelectUtilityBox extends OkCancelBox {

        private ListBox list;

        private List<ServiceItemType> selectedItems;

        public SelectUtilityBox() {
            super("Select Utilities");
        }

        @Override
        protected Widget createContent() {
            okButton.setEnabled(false);

            if (!getValue().availableUtilities().isEmpty()) {
                list = new ListBox(true);
                list.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        okButton.setEnabled(list.getSelectedIndex() >= 0);
                    }
                });

                List<ServiceItemType> alreadySelected = new ArrayList<ServiceItemType>();
                for (ServiceItemType item : getValue().serviceCatalog().includedUtilities()) {
                    alreadySelected.add(item);
                }

                for (ServiceItemType item : getValue().availableUtilities()) {
                    if (!alreadySelected.contains(item)) {
                        list.addItem(item.getStringView());
                        list.setValue(list.getItemCount() - 1, item.id().toString());
                    }
                }
                list.setVisibleItemCount(8);
                list.setWidth("100%");
                return list.asWidget();
            } else {
                return new HTML(i18n.tr("There are no concessions!.."));
            }
        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        @Override
        protected boolean onOk() {
            selectedItems = new ArrayList<ServiceItemType>(4);
            for (int i = 0; i < list.getItemCount(); ++i) {
                if (list.isItemSelected(i)) {
                    for (ServiceItemType item : getValue().availableUtilities()) {
                        if (list.getValue(i).contentEquals(item.id().toString())) {
                            selectedItems.add(item);
                        }
                    }
                }
            }
            return super.onOk();
        }

        @Override
        protected void onCancel() {
            selectedItems = null;
        }

        protected List<ServiceItemType> getSelectedItems() {
            return selectedItems;
        }
    }
}
