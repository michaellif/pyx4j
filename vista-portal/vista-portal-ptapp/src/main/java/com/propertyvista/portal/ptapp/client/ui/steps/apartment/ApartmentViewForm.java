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

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.client.ui.OptionsFilter;
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
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
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
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.financial.offering.extradata.Vehicle;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.portal.ptapp.client.ui.components.BuildingPicture;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;

public class ApartmentViewForm extends CEntityEditor<ApartmentInfoDTO> {

    private static I18n i18n = I18n.get(ApartmentViewForm.class);

    private final FormFlexPanel consessionPanel = new FormFlexPanel();

    private final FormFlexPanel chargedPanel = new FormFlexPanel();

    private final FormFlexPanel petsPanel = new FormFlexPanel();

    private final FormFlexPanel parkingPanel = new FormFlexPanel();

    private final FormFlexPanel storagePanel = new FormFlexPanel();

    private final FormFlexPanel otherPanel = new FormFlexPanel();

    public ApartmentViewForm() {
        super(ApartmentInfoDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("General Info"));

        FormFlexPanel info = new FormFlexPanel();

        int row1 = -1;
        info.setWidget(++row1, 0, new DecoratorBuilder(inject(proto().name()), 20).build());
        info.setWidget(++row1, 0, new VistaLineSeparator(100, Unit.PCT));
        info.getFlexCellFormatter().setColSpan(row1, 0, 2);

        info.setWidget(++row1, 0, new DecoratorBuilder(inject(proto().suiteNumber()), 10).build());

        row1 = 1;
        info.setWidget(++row1, 1, new DecoratorBuilder(inject(proto().bedrooms()), 10).build());
        info.setWidget(++row1, 1, new DecoratorBuilder(inject(proto().bathrooms()), 10).build());

        info.getColumnFormatter().setWidth(0, "50%");
        info.getColumnFormatter().setWidth(1, "50%");
        info.setWidth("75%");

        main.setWidget(++row, 0, info);

        main.setH1(++row, 0, 1, i18n.tr("Lease Terms"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseFrom()), 8).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseTo()), 8).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unitRent()), 8).build());

        main.setH1(++row, 0, 1, i18n.tr("Promotions, Discounts and Concessions"));
        consessionPanel.setWidget(0, 0, inject(proto().concessions(), createConcessionsFolderEditor()));
        main.setWidget(++row, 0, consessionPanel);

        main.setH1(++row, 0, 1, i18n.tr("Included"));
        main.setWidget(++row, 0, inject(proto().includedUtilities(), new UtilityFolder()));

        main.setH1(++row, 0, 1, i18n.tr("Excluded"));
        main.setWidget(++row, 0, inject(proto().externalUtilities(), new UtilityFolder()));

        main.setH1(++row, 0, 1, i18n.tr("Charged Utilities"));
        chargedPanel.setWidget(0, 0, inject(proto().agreedUtilities(), new FeatureFolder(Feature.Type.utility, this, false)));
        main.setWidget(++row, 0, chargedPanel);

        main.setH1(++row, 0, 1, i18n.tr("Add-Ons"));

        main.setH3(++row, 0, 1, i18n.tr("Pets"));
        petsPanel.setWidget(0, 0, inject(proto().agreedPets(), new FeatureExFolder(true, Feature.Type.pet, this)));
        main.setWidget(++row, 0, petsPanel);

        main.setH3(++row, 0, 1, i18n.tr("Parking"));
        parkingPanel.setWidget(0, 0, inject(proto().agreedParking(), new FeatureExFolder(true, Feature.Type.parking, this)));
        main.setWidget(++row, 0, parkingPanel);

        main.setH3(++row, 0, 1, i18n.tr("Storage"));
        storagePanel.setWidget(0, 0, inject(proto().agreedStorage(), new FeatureFolder(Feature.Type.locker, this, true)));
        main.setWidget(++row, 0, storagePanel);

        main.setH3(++row, 0, 1, i18n.tr("Other"));
        otherPanel.setWidget(0, 0, inject(proto().agreedOther(), new FeatureFolder(Feature.Type.addOn, this, true)));
        main.setWidget(++row, 0, otherPanel);

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

    // decoration stuff:
    protected class DecoratorBuilder extends WidgetDecorator.Builder {

        public DecoratorBuilder(CComponent<?> component) {
            super(component);
            readOnlyMode(!isEditable());
        }

        public DecoratorBuilder(CComponent<?> component, double componentWidth) {
            super(component);
            readOnlyMode(!isEditable());
            componentWidth(componentWidth);
        }

        public DecoratorBuilder(CComponent<?> component, double componentWidth, double labelWidth) {
            super(component);
            readOnlyMode(!isEditable());
            componentWidth(componentWidth);
            labelWidth(labelWidth);
        }

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

        private final Feature.Type type;

        private final ApartmentViewForm apartmentViewForm;

        public FeatureFolder(Feature.Type type, ApartmentViewForm apartmentViewForm, boolean modifyable) {
            super(ChargeItem.class, modifyable);
            this.type = type;
            this.apartmentViewForm = apartmentViewForm;
        }

        @Override
        protected List<EntityFolderColumnDescriptor> columns() {
            ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().item().type(), "10em"));
            columns.add(new EntityFolderColumnDescriptor(proto().adjustedPrice(), "7em"));
            columns.add(new EntityFolderColumnDescriptor(proto().item().description(), "30em"));
            return columns;

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

    }

    static class FeatureExFolder extends VistaBoxFolder<ChargeItem> {

        private final Feature.Type type;

        private final ApartmentViewForm apartmentViewForm;

        public FeatureExFolder(boolean modifyable, Feature.Type type, ApartmentViewForm apartmentViewForm) {
            super(ChargeItem.class, modifyable);
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

                        panel.add(new HTML(HtmlUtils.h5(ApartmentViewForm.i18n.tr("Vehicle data") + ":")));
                        panel.add(split);

                        split.getLeftPanel().add(inject(proto().year()), 5);
                        split.getLeftPanel().add(inject(proto().make()), 10);
                        split.getLeftPanel().add(inject(proto().model()), 10);

                        split.getRightPanel().add(inject(proto().plateNumber()), 10);

                        CEditableComponent<Country, ?> country;
                        split.getRightPanel().add(country = (CEditableComponent<Country, ?>) inject(proto().country()), 13);
                        CEditableComponent<Province, ?> province;
                        split.getRightPanel().add(province = (CEditableComponent<Province, ?>) inject(proto().province()), 17);

                        ProvinceContryFilters.attachFilters(province, country, new OptionsFilter<Province>() {
                            @Override
                            public boolean acceptOption(Province entity) {
                                if (getValue() == null) {
                                    return true;
                                } else {
                                    Country country = (Country) getValue().getMember(proto().country().getPath());
                                    return country.isNull() || EqualsHelper.equals(entity.country().name(), country.name());
                                }
                            }
                        });
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

                        panel.add(new HTML(HtmlUtils.h5(ApartmentViewForm.i18n.tr("Pet data") + ":")));
                        panel.add(split);

                        split.getLeftPanel().add(inject(proto().name()), 15);
                        split.getLeftPanel().add(inject(proto().color()), 15);
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

        private final ApartmentInfoDTO apartmentInfo;

        public SelectFeatureBox(Feature.Type type, ApartmentInfoDTO apartmentInfo) {
            super(i18n.tr("Select {0}(s)", type));
            this.type = type;
            this.apartmentInfo = apartmentInfo;
            setContent(createContent());
        }

        protected Widget createContent() {
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
                    return new HTML(i18n.tr("All {0}(s) have been selected already!", type));
                }
            } else {
                return new HTML(i18n.tr("There are no {0}(s) available!", type));
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
