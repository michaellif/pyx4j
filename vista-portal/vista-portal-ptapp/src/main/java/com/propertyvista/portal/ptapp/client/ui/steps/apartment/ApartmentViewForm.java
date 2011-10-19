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

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.financial.offering.extradata.Vehicle;
import com.propertyvista.portal.ptapp.client.ui.components.BuildingPicture;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;

public class ApartmentViewForm extends CEntityEditor<ApartmentInfoDTO> {

    private static I18n i18n = I18n.get(ApartmentViewForm.class);

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
        main.add(inject(proto().includedUtilities(), new UtilityFolder()));

        main.add(new VistaHeaderBar(i18n.tr("Excluded")));
        main.add(inject(proto().externalUtilities(), new UtilityFolder()));

        main.add(chargedPanel = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        chargedPanel.add(new VistaHeaderBar(i18n.tr("Charged Utilities")));
        chargedPanel.add(inject(proto().agreedUtilities(), new FeatureFolder(Feature.Type.utility, this, false)));

        main.add(new VistaHeaderBar(i18n.tr("Add-ons")));

        main.add(petsPanel = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        petsPanel.add(new HTML(HtmlUtils.h5(i18n.tr("Pets:"))));
        petsPanel.add(inject(proto().agreedPets(), new FeatureExFolder(Feature.Type.pet, this, true)));

        main.add(parkingPanel = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        parkingPanel.add(new VistaLineSeparator(100, Unit.PCT));
        parkingPanel.add(new HTML(HtmlUtils.h5(i18n.tr("Parking:"))));
        parkingPanel.add(inject(proto().agreedParking(), new FeatureExFolder(Feature.Type.parking, this, true)));

        main.add(storagePanel = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        storagePanel.add(new VistaLineSeparator(100, Unit.PCT));
        storagePanel.add(new HTML(HtmlUtils.h5(i18n.tr("Storage:"))));
        storagePanel.add(inject(proto().agreedStorage(), new FeatureFolder(Feature.Type.locker, this, true)));

        main.add(otherPanel = new VistaDecoratorsFlowPanel(true, main.getDefaultLabelWidth()));
        otherPanel.add(new VistaLineSeparator(100, Unit.PCT));
        otherPanel.add(new HTML(HtmlUtils.h5(i18n.tr("Other:"))));
        otherPanel.add(inject(proto().agreedOther(), new FeatureFolder(Feature.Type.addOn, this, true)));

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

    //TODO remove header
    static class UtilityFolder extends VistaTableFolder<ServiceItemType> {

        public static final ArrayList<EntityFolderColumnDescriptor> COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
        static {
            ServiceItemType proto = EntityFactory.getEntityPrototype(ServiceItemType.class);
            COLUMNS.add(new EntityFolderColumnDescriptor(proto.name(), "30em"));
        }

        public UtilityFolder() {
            super(ServiceItemType.class, false);
        }

        @Override
        public CEditableComponent<?, ?> create(IObject<?> member) {
            if (member instanceof ServiceItemType) {
                return new UtilityEditor();
            } else {
                return super.create(member);
            }
        }

        @Override
        protected List<EntityFolderColumnDescriptor> columns() {
            ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().name(), "30"));
            return columns;
        }

        static class UtilityEditor extends CEntityFolderRowEditor<ServiceItemType> {
            public UtilityEditor() {
                super(ServiceItemType.class, UtilityFolder.COLUMNS);
            }

            @Override
            protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                if (column.getObject() == proto().name()) {
                    return inject(column.getObject(), new CLabel());
                }
                return super.createCell(column);
            }
        }

        @Override
        protected IFolderDecorator<ServiceItemType> createDecorator() {
            TableFolderDecorator<ServiceItemType> decotator = (TableFolderDecorator<ServiceItemType>) super.createDecorator();
            decotator.setShowHeader(false);
            return decotator;
        }
    }

    static class FeatureFolder extends VistaTableFolder<ChargeItem> {

        public static final ArrayList<EntityFolderColumnDescriptor> COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
        static {
            ChargeItem proto = EntityFactory.getEntityPrototype(ChargeItem.class);
            COLUMNS.add(new EntityFolderColumnDescriptor(proto.item().type(), "10em"));
            COLUMNS.add(new EntityFolderColumnDescriptor(proto.adjustedPrice(), "7em"));
            COLUMNS.add(new EntityFolderColumnDescriptor(proto.item().description(), "30em"));
        }

        private final Feature.Type type;

        private final ApartmentViewForm apartmentViewForm;

        public FeatureFolder(Feature.Type type, ApartmentViewForm apartmentViewForm, boolean editable) {
            super(ChargeItem.class, editable);
            this.type = type;
            this.apartmentViewForm = apartmentViewForm;
        }

        @Override
        public CEditableComponent<?, ?> create(IObject<?> member) {
            if (member instanceof ChargeItem) {
                return new FeatureEditor();
            } else {
                return super.create(member);
            }
        }

        @Override
        protected List<EntityFolderColumnDescriptor> columns() {
            return COLUMNS;
        }

        @Override
        protected void addItem() {
            new ShowPopUpBox<SelectFeatureBox>(new SelectFeatureBox(type, apartmentViewForm.getValue())) {
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

        protected void unconditionalRemoveItem(CEntityFolderItem<ChargeItem> item) {
            super.removeItem(item);
        }

        @Override
        protected void removeItem(final CEntityFolderItem<ChargeItem> item) {
            if (!item.getValue().adjustments().isEmpty()) {
                MessageDialog.confirm(i18n.tr("Warning!"),
                        i18n.tr("By removing this item you will lose the agreed price adjustment! Are you sure you want to remove it?"), new Runnable() {
                            @Override
                            public void run() {
                                unconditionalRemoveItem(item);
                            }
                        });
            } else {
                super.removeItem(item);
            }
        }

        static class FeatureEditor extends CEntityFolderRowEditor<ChargeItem> {
            public FeatureEditor() {
                super(ChargeItem.class, FeatureFolder.COLUMNS);
            }
        }
    }

    static class FeatureExFolder extends VistaBoxFolder<ChargeItem> {

        private final Feature.Type type;

        private final ApartmentViewForm apartmentViewForm;

        public FeatureExFolder(Feature.Type type, ApartmentViewForm apartmentViewForm, boolean editable) {
            super(ChargeItem.class, editable);
            this.type = type;
            this.apartmentViewForm = apartmentViewForm;
        }

        @Override
        public CEditableComponent<?, ?> create(IObject<?> member) {
            if (member instanceof ChargeItem) {
                return new FeatureExEditor();
            } else {
                return super.create(member);
            }
        }

        @Override
        protected void addItem() {
            new ShowPopUpBox<SelectFeatureBox>(new SelectFeatureBox(type, apartmentViewForm.getValue())) {
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
    }

    static class FeatureExEditor extends CEntityEditor<ChargeItem> {

        public FeatureExEditor() {
            super(ChargeItem.class);
        }

        private final SimplePanel extraDataPanel = new SimplePanel();

        @Override
        public IsWidget createContent() {
            VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable(), 10);
            VistaDecoratorsSplitFlowPanel split;

            CLabel lb;
            main.add(split = new VistaDecoratorsSplitFlowPanel(!isEditable(), 10, 22));
            split.getLeftPanel().add(inject(proto().item().type().name(), lb = new CLabel()));
            lb.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

            CNumberLabel nl = new CNumberLabel();
            split.getRightPanel().add(inject(proto().price(), nl), 6);
            nl.setNumberFormat(proto().price().getMeta().getFormat());

            nl = new CNumberLabel();
            split.getRightPanel().add(inject(proto().adjustedPrice(), nl), 6);
            nl.setNumberFormat(proto().adjustedPrice().getMeta().getFormat());
            nl.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

            main.add(extraDataPanel);

            return main;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void populate(ChargeItem value) {
            super.populate(value);

//          if (value.item().type().featureType().getValue() == Feature.Type.utility) {
//              setRemovable(false);
//          }

            get(proto().adjustedPrice()).setVisible(!value.adjustments().isEmpty());

            CEntityEditor editor = null;
            switch (value.item().type().featureType().getValue()) {
            case parking:
                editor = new CEntityEditor<Vehicle>(Vehicle.class, new VistaEditorsComponentFactory()) {
                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel panel = new VistaDecoratorsFlowPanel(!isEditable(), 10);
                        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable(), 10, 30);

                        panel.add(new HTML(HtmlUtils.h5(ApartmentViewForm.i18n.tr("Vehicle data:"))));
                        panel.add(split);

                        split.getLeftPanel().add(inject(proto().year()), 5);
                        split.getLeftPanel().add(inject(proto().make()), 10);
                        split.getLeftPanel().add(inject(proto().model()), 10);

                        split.getRightPanel().add(inject(proto().plateNumber()), 10);
                        split.getRightPanel().add(inject(proto().country()), 10);
                        split.getRightPanel().add(inject(proto().province()), 17);

                        return panel;
                    }

                    @Override
                    public CEditableComponent<?, ?> create(IObject<?> member) {
                        return factory.create(member); // use own (editor) factory instead of parent (viewer) one!..
                    }
                };

                if (value.extraData().isNull()) {
                    value.extraData().set(EntityFactory.create(Vehicle.class));
                }
                break;
            case pet:
                editor = new CEntityEditor<Pet>(Pet.class, new VistaEditorsComponentFactory()) {
                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel panel = new VistaDecoratorsFlowPanel(!isEditable(), 10);
                        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable(), 10, 30);

                        panel.add(new HTML(HtmlUtils.h5(ApartmentViewForm.i18n.tr("Pet data:"))));
                        panel.add(split);

                        split.getLeftPanel().add(inject(proto().name()), 15);
                        split.getLeftPanel().add(inject(proto().color()), 10);
                        split.getLeftPanel().add(inject(proto().breed()), 15);

                        split.getRightPanel().add(inject(proto().weight()), 4);
                        split.getRightPanel().add(inject(proto().weightUnit()), 4);
                        split.getRightPanel().add(inject(proto().birthDate()), 8.2);

                        return panel;
                    }

                    @Override
                    public CEditableComponent<?, ?> create(IObject<?> member) {
                        return factory.create(member); // use own (editor) factory instead of parent (viewer) one!..
                    }
                };

                if (value.extraData().isNull()) {
                    value.extraData().set(EntityFactory.create(Pet.class));
                }
                break;
            }

            if (editor != null) {
                editor.onBound(this);
                editor.populate(value.extraData().cast());
                extraDataPanel.setWidget(editor);
            }
        }
    }

    private CEntityFolder<Concession> createConcessionsFolderEditor() {
        return new VistaTableFolder<Concession>(Concession.class, false) {
            private final VistaTableFolder<Concession> parent = this;

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
                VistaTableFolderDecorator<Concession> decor = new VistaTableFolderDecorator<Concession>(columns(), parent);
//                decor.setShowHeader(false);
                return decor;
            }
        };
    }

//
// Selection Boxes:

    private static class SelectFeatureBox extends OkCancelBox {

        private ListBox list;

        private List<ServiceItem> selectedItems;

        private final Feature.Type type;

        private SimplePanel content;

        private final ApartmentInfoDTO apartmentInfo;

        public SelectFeatureBox(Feature.Type type, ApartmentInfoDTO apartmentInfo) {
            super(i18n.tr("Select {0}(s)", type));
            this.type = type;
            this.apartmentInfo = apartmentInfo;
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

//  TODO not sure if we need duplicate item restriction:                
//                List<ServiceItem> alreadySelected = new ArrayList<ServiceItem>();
//                for (ChargeItem item : getAgreedList()) {
//                    alreadySelected.add(item.item());
//                }

                for (ServiceItem item : getAvailableList()) {
                    if (isCompatible(item) /* && !alreadySelected.contains(item) */) {
                        list.addItem(item.getStringView(), item.id().toString());
                    }
                }

                if (list.getItemCount() > 0) {
                    list.setVisibleItemCount(8);
                    list.setWidth("100%");
                    return list.asWidget();
                } else {
                    return new HTML(i18n.tr("All {0}(s) have been selected already!..", type));
                }
            } else {
                return new HTML(i18n.tr("There are no {0}(s) available!..", type));
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
                return apartmentInfo.agreedUtilities();
            case pet:
                return apartmentInfo.agreedPets();
            case parking:
                return apartmentInfo.agreedParking();
            case locker:
                return apartmentInfo.agreedStorage();
            default:
                return apartmentInfo.agreedOther();
            }
        }

        private List<ServiceItem> getAvailableList() {
            switch (type) {
            case utility:
                return apartmentInfo.availableUtilities();
            case pet:
                return apartmentInfo.availablePets();
            case parking:
                return apartmentInfo.availableParking();
            case locker:
                return apartmentInfo.availableStorage();
            default:
                return apartmentInfo.availableOther();
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
