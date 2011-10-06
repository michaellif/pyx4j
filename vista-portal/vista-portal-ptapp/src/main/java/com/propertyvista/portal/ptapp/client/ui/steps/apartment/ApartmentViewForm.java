/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.apartment;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.portal.ptapp.client.ui.components.BuildingPicture;
import com.propertyvista.portal.ptapp.client.ui.components.PtAppEntityFolder;
import com.propertyvista.portal.ptapp.client.ui.components.PtAppTableFolderDecorator;
import com.propertyvista.portal.ptapp.client.ui.components.PtAppTableFolderItemDecorator;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;

public class ApartmentViewForm extends CEntityEditor<ApartmentInfoDTO> {

    private static I18n i18n = I18nFactory.getI18n(ApartmentViewForm.class);

    private VistaDecoratorsFlowPanel consessionPanel;

    private VistaDecoratorsFlowPanel chargedPanel;

    private VistaDecoratorsFlowPanel petsPanel;

    private VistaDecoratorsFlowPanel parkingPanel;

    private VistaDecoratorsFlowPanel storagePanel;

    private VistaDecoratorsFlowPanel otherPanel;

    public ApartmentViewForm() {
        super(ApartmentInfoDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(true, 10);
        VistaDecoratorsFlowPanel part;

        main.add(new VistaHeaderBar(i18n.tr("General Info")));
        main.add(part = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        part.getElement().getStyle().setPaddingLeft(2, Unit.EM);
        part.add(inject(proto().name()), 20);

        main.add(new VistaLineSeparator(100, Unit.PCT));

        VistaDecoratorsSplitFlowPanel split;
        main.add(split = new VistaDecoratorsSplitFlowPanel(true, main.getDefaultLabelWidth(), 20));
        split.getElement().getStyle().setPaddingLeft(2, Unit.EM);

        split.getLeftPanel().add(inject(proto().suiteNumber()), 10);

        split.getRightPanel().add(inject(proto().bedrooms()), 10);
        split.getRightPanel().add(inject(proto().bathrooms()), 10);

        main.add(new VistaHeaderBar(i18n.tr("Lease Terms")));
        main.add(part = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        part.getElement().getStyle().setPaddingLeft(2, Unit.EM);
        part.add(inject(proto().leaseFrom()), 8);
        part.add(inject(proto().leaseTo()), 8);
        part.add(inject(proto().unitRent()), 8);

        main.add(consessionPanel = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        consessionPanel.add(new VistaHeaderBar(i18n.tr(i18n.tr("Promotions, Discounts and Concessions"))));
        consessionPanel.add(inject(proto().concessions(), createConcessionsFolderEditor()));

        main.add(new VistaHeaderBar(i18n.tr("Included")));
        main.add(inject(proto().includedUtilities(), createUtilitiesFolderEditor()));

        main.add(new VistaHeaderBar(i18n.tr("Excluded")));
        main.add(inject(proto().externalUtilities(), createUtilitiesFolderEditor()));

        main.add(chargedPanel = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        chargedPanel.add(new VistaHeaderBar(i18n.tr("Charged Utilities")));
        chargedPanel.add(inject(proto().agreedUtilities(), createFeaturesFolderEditor(Feature.Type.utility, false)));

        main.add(new VistaHeaderBar(i18n.tr("Add-ons")));

        main.add(petsPanel = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        petsPanel.add(new HTML(HtmlUtils.h5(i18n.tr("Pets:"))));
        petsPanel.add(inject(proto().agreedPets(), createFeaturesFolderEditor(Feature.Type.pet, true)));

        main.add(parkingPanel = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        parkingPanel.add(new VistaLineSeparator(100, Unit.PCT));
        parkingPanel.add(new HTML(HtmlUtils.h5(i18n.tr("Parking:"))));
        parkingPanel.add(inject(proto().agreedParking(), createFeaturesFolderEditor(Feature.Type.parking, true)));

        main.add(storagePanel = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        storagePanel.add(new VistaLineSeparator(100, Unit.PCT));
        storagePanel.add(new HTML(HtmlUtils.h5(i18n.tr("Storage:"))));
        storagePanel.add(inject(proto().agreedStorage(), createFeaturesFolderEditor(Feature.Type.locker, true)));

        main.add(otherPanel = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        otherPanel.add(new VistaLineSeparator(100, Unit.PCT));
        otherPanel.add(new HTML(HtmlUtils.h5(i18n.tr("Other:"))));
        otherPanel.add(inject(proto().agreedOther(), createFeaturesFolderEditor(Feature.Type.addOn, true)));

        // last step - add building picture on the right:
        HorizontalPanel content = new HorizontalPanel();
        content.add(main);
        content.add(new BuildingPicture());
        return content;
    }

    @Override
    public void populate(ApartmentInfoDTO value) {
        super.populate(value);

        //hide/show various panels depend on populated data:
        consessionPanel.setVisible(!value.concessions().isEmpty());
        chargedPanel.setVisible(!value.agreedUtilities().isEmpty());

        petsPanel.setVisible(!value.agreedPets().isEmpty() || !value.availablePets().isEmpty());
        parkingPanel.setVisible(!value.agreedParking().isEmpty() || !value.availableParking().isEmpty());
        storagePanel.setVisible(!value.agreedStorage().isEmpty() || !value.availableStorage().isEmpty());
        otherPanel.setVisible(!value.agreedOther().isEmpty() || !value.availableOther().isEmpty());
    }

//
// List Viewers:

    private CEntityFolder<ServiceItemType> createUtilitiesFolderEditor() {
        return new PtAppEntityFolder<ServiceItemType>(ServiceItemType.class, false) {
            private final PtAppEntityFolder<ServiceItemType> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "30"));
                return columns;
            }

            @Override
            protected IFolderDecorator<ServiceItemType> createDecorator() {
                PtAppTableFolderDecorator<ServiceItemType> decor = new PtAppTableFolderDecorator<ServiceItemType>(columns(), parent);
                decor.setShowHeader(false);
                return decor;
            }

            @Override
            protected CEntityFolderRowEditor<ServiceItemType> createItem() {
                return new CEntityFolderRowEditor<ServiceItemType>(ServiceItemType.class, columns()) {
                    @Override
                    public IFolderItemDecorator<ServiceItemType> createDecorator() {
                        return new PtAppTableFolderItemDecorator<ServiceItemType>(parent);
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

    private CEntityFolder<ChargeItem> createFeaturesFolderEditor(final Feature.Type type, boolean editable) {
        return new PtAppEntityFolder<ChargeItem>(ChargeItem.class, editable) {
            private final PtAppEntityFolder<ChargeItem> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().item().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().adjustedPrice(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().item().description(), "30em"));
                return columns;
            }

            @Override
            protected IFolderDecorator<ChargeItem> createDecorator() {
                PtAppTableFolderDecorator<ChargeItem> decor = new PtAppTableFolderDecorator<ChargeItem>(columns(), parent);
                setExternalAddItemProcessing(true);
                decor.addItemAddClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        new ShowPopUpBox<SelectFeatureBox>(new SelectFeatureBox(type)) {
                            @Override
                            protected void onClose(SelectFeatureBox box) {
                                if (box.getSelectedItems() != null) {
                                    for (ServiceItem item : box.getSelectedItems()) {
                                        ChargeItem newItem = EntityFactory.create(ChargeItem.class);
                                        newItem.item().set(item);
                                        newItem.price().setValue(item.price().getValue());
                                        newItem.adjustedPrice().setValue(item.price().getValue());
                                        addItem(newItem);
                                    }
                                }
                            }
                        };
                    }
                });
                return decor;
            }

            protected void unconditionalRemoveItem(CEntityFolderItemEditor<ChargeItem> item) {
                super.removeItem(item);
            }

            @Override
            protected void removeItem(final CEntityFolderItemEditor<ChargeItem> item) {
                if (!item.getValue().adjustments().isEmpty()) {
                    MessageDialog.confirm(i18n.tr("Warning!"),
                            i18n.tr("Removing this item you will lost price adjustment agreed in the office! Are you sure to remove it?"), new Runnable() {
                                @Override
                                public void run() {
                                    unconditionalRemoveItem(item);
                                }
                            });
                } else {
                    super.removeItem(item);
                }
            }
        };
    }

    private CEntityFolder<Concession> createConcessionsFolderEditor() {
        return new PtAppEntityFolder<Concession>(Concession.class, false) {
            private final PtAppEntityFolder<Concession> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().value(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().term(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().condition(), "10em"));
                return columns;
            }

            @Override
            protected IFolderDecorator<Concession> createDecorator() {
                PtAppTableFolderDecorator<Concession> decor = new PtAppTableFolderDecorator<Concession>(columns(), parent);
//                decor.setShowHeader(false);
                return decor;
            }
        };
    }

//
// Selection Boxes:

    private class SelectFeatureBox extends OkCancelBox {

        private ListBox list;

        private List<ServiceItem> selectedItems;

        private final Feature.Type type;

        private SimplePanel content;

        public SelectFeatureBox(Feature.Type type) {
            super("Select " + type.toString() + "(s)");
            this.type = type;

            // createContent called from within surper's constructor but we need to use our constructor parameters...
            content.setWidget(createRealContent());
        }

        @Override
        protected Widget createContent() {
            okButton.setEnabled(false);
            return (content = new SimplePanel());
        }

        // createContent called from within surper's constructor but we need to use our constructor parameters...
        protected Widget createRealContent() {
            okButton.setEnabled(false);

            if (!getAvailableList().isEmpty()) {
                list = new ListBox(true);
                list.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        okButton.setEnabled(list.getSelectedIndex() >= 0);
                    }
                });

                for (ServiceItem item : getAvailableList()) {
                    if (isCompatible(item) && !getAgreedList().contains(item)) {
                        list.addItem(item.getStringView());
                        list.setValue(list.getItemCount() - 1, item.id().toString());
                    }
                }

                if (list.getItemCount() > 0) {
                    list.setVisibleItemCount(8);
                    list.setWidth("100%");
                    return list.asWidget();
                } else {
                    return new HTML(i18n.tr("All ") + type.toString() + i18n.tr("(s) have been selected already!.."));
                }
            } else {
                return new HTML(i18n.tr("There are no ") + type.toString() + i18n.tr("(s) available!.."));
            }
        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        @Override
        protected boolean onOk() {
            selectedItems = new ArrayList<ServiceItem>(4);
            for (int i = 0; i < list.getItemCount(); ++i) {
                if (list.isItemSelected(i)) {
                    for (ServiceItem item : getAvailableList()) {
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

        protected List<ServiceItem> getSelectedItems() {
            return selectedItems;
        }

        private List<ChargeItem> getAgreedList() {
            switch (type) {
            case utility:
                return getValue().agreedUtilities();
            case pet:
                return getValue().agreedPets();
            case parking:
                return getValue().agreedParking();
            case locker:
                return getValue().agreedStorage();
            default:
                return getValue().agreedOther();
            }
        }

        private List<ServiceItem> getAvailableList() {
            switch (type) {
            case utility:
                return getValue().availableUtilities();
            case pet:
                return getValue().availablePets();
            case parking:
                return getValue().availableParking();
            case locker:
                return getValue().availableStorage();
            default:
                return getValue().availableOther();
            }
        }

        private boolean isCompatible(ServiceItem item) {

            if (type.equals(Feature.Type.addOn)) {
                switch (item.type().featureType().getValue()) {
                case utility:
                case pet:
                case parking:
                case locker:
                    return false;

                default:
                    return true;
                }
            }

            return (item.type().featureType().getValue().equals(type));
        }
    }
}
