/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.editors.YardiDataEditor;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemExtraData;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;
import com.propertyvista.domain.tenant.lease.extradata.YardiLeaseChargeData;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;
import com.propertyvista.portal.shared.ui.util.editors.PetDataEditor;
import com.propertyvista.portal.shared.ui.util.editors.VehicleDataEditor;

public class OptionsStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(OptionsStep.class);

    private final BasicFlexFormPanel chargedPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel petsPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel parkingPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel storagePanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel otherPanel = new BasicFlexFormPanel();

    private FeatureFolder lockerFolder;

    private FeatureExFolder petFolder;

    private FeatureExFolder parkingFolder;

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Unit Options"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setWidget(++row, 0, inject(proto().unitOptionsSelection(), new StepDataForm()));

        return panel;
    }

    public void setStepValue(UnitOptionsSelectionDTO value) {
        get(proto().unitOptionsSelection()).setValue(value);
    }

    public UnitOptionsSelectionDTO getStepValue() {
        return getWizard().getValue().unitOptionsSelection();
    }

    private class StepDataForm extends CEntityForm<UnitOptionsSelectionDTO> {

        public StepDataForm() {
            super(UnitOptionsSelectionDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();

            int row = -1;
            petsPanel.setH2(0, 0, 1, i18n.tr("Pets"));
            petsPanel.setWidget(1, 0, inject(proto().selectedPets(), petFolder = new FeatureExFolder(ARCode.Type.Pet)));
            content.setWidget(++row, 0, petsPanel);

            parkingPanel.setH2(0, 0, 1, i18n.tr("Parking"));
            parkingPanel.setWidget(1, 0, inject(proto().selectedParking(), parkingFolder = new FeatureExFolder(ARCode.Type.Parking)));
            content.setWidget(++row, 0, parkingPanel);

            storagePanel.setH2(0, 0, 1, i18n.tr("Storage"));
            storagePanel.setWidget(1, 0, inject(proto().selectedStorage(), lockerFolder = new FeatureFolder(ARCode.Type.Locker)));
            content.setWidget(++row, 0, storagePanel);

            chargedPanel.setH1(0, 0, 1, i18n.tr("Utilities"));
            chargedPanel.setWidget(1, 0, inject(proto().selectedUtilities(), new FeatureFolder(ARCode.Type.Utility)));
            content.setWidget(++row, 0, chargedPanel);

            otherPanel.setH2(0, 0, 1, i18n.tr("Other"));
            otherPanel.setWidget(1, 0, inject(proto().selectedOther(), new FeatureFolder(ARCode.Type.OneTime)));
            content.setWidget(++row, 0, otherPanel);

            return content;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            petsPanel.setVisible(!getValue().selectedPets().isEmpty() || !getValue().availablePets().isEmpty());
            parkingPanel.setVisible(!getValue().selectedParking().isEmpty() || !getValue().availableParking().isEmpty());
            storagePanel.setVisible(!getValue().selectedStorage().isEmpty() || !getValue().availableStorage().isEmpty());
            chargedPanel.setVisible(!getValue().selectedUtilities().isEmpty() || !getValue().availableOther().isEmpty());
            otherPanel.setVisible(!getValue().selectedOther().isEmpty() || !getValue().availableOther().isEmpty());

            if (!getValue().restrictions().isEmpty()) {
                petFolder.setMaxCount(getValue().restrictions().maxPets().getValue());
                parkingFolder.setMaxCount(getValue().restrictions().maxParkingSpots().getValue());
                lockerFolder.setMaxCount(getValue().restrictions().maxParkingSpots().getValue());
            }
        }
    }

    private class FeatureFolder extends PortalBoxFolder<BillableItem> {

        private final ARCode.Type type;

        private int maxCount = -1;

        public FeatureFolder(ARCode.Type type) {
            super(BillableItem.class);
            this.type = type;
        }

        protected int getMaxCount() {
            return maxCount;
        };

        protected void setMaxCount(int maxCount) {
            this.maxCount = maxCount;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof BillableItem) {
                return new FeatureItemForm();
            } else {
                return super.create(member);
            }
        }

        class FeatureItemForm extends CEntityForm<BillableItem> {

            public FeatureItemForm() {
                super(BillableItem.class);
                setEditable(false);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();

                int row = -1;
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().item().name())).build());
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().agreedPrice())).build());
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().item().description())).build());

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                @SuppressWarnings("unchecked")
                CEntityFolderItem<BillableItem> item = (CEntityFolderItem<BillableItem>) getParent();
                item.setRemovable(!isMandatoryFeature(getValue().item().product()));
            }

            private boolean isMandatoryFeature(Product.ProductV product) {
                return product.isInstanceOf(Feature.FeatureV.class) && ((Feature.FeatureV) product.cast()).mandatory().isBooleanTrue();
            }
        }

        @Override
        protected void addItem() {
            if (getMaxCount() < 0 || getValue().size() < getMaxCount()) {
                new SelectFeatureBox(type, OptionsStep.this.getStepValue()) {
                    @Override
                    public boolean onClickOk() {
                        for (ProductItem item : getSelectedItems()) {
                            if (getMaxCount() < 0 || getValue().size() < getMaxCount()) {
                                BillableItem newItem = EntityFactory.create(BillableItem.class);
                                newItem.item().set(item);
                                newItem.agreedPrice().setValue(item.price().getValue());
                                addItem(newItem);
                            }
                        }
                        return true;
                    }
                }.show();
            } else {
                MessageDialog.warn(i18n.tr("Sorry"), i18n.tr("You cannot add more than {0} items here!", getMaxCount()));
            }
        }

        @Override
        protected void removeItem(final CEntityFolderItem<BillableItem> item) {
            MessageDialog.confirm(i18n.tr("Feature removal"), i18n.tr("Do you really want to remove the Feature?"), new Command() {
                @Override
                public void execute() {
                    FeatureFolder.super.removeItem(item);
                }
            });
        }

        @Override
        public void addValidations() {
            addValueValidator(new EditableValueValidator<IList<BillableItem>>() {
                @Override
                public ValidationError isValid(CComponent<IList<BillableItem>> component, IList<BillableItem> value) {
                    if (value == null) {
                        return null;
                    }
                    return (value.size() < getMaxCount()) ? null : new ValidationError(component, i18n.tr("You cannot add more than {0} items here!",
                            getMaxCount()));
                }
            });
            super.addValidations();
        }
    }

    private class FeatureExFolder extends FeatureFolder {

        public FeatureExFolder(ARCode.Type type) {
            super(type);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof BillableItem) {
                return new FeatureExItemForm();
            } else {
                return super.create(member);
            }
        }

        class FeatureExItemForm extends FeatureItemForm {

            private final SimplePanel extraDataPanel = new SimplePanel();

            public FeatureExItemForm() {
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();

                int row = -1;
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().item().name())).build());
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().agreedPrice())).build());
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().item().description())).build());
                content.setWidget(++row, 0, extraDataPanel);

                return content;
            }

            @Override
            protected void onValuePropagation(BillableItem value, boolean fireEvent, boolean populate) {
                setExtraDataEditor(value, populate);
                super.onValuePropagation(value, fireEvent, populate);
            }

            @SuppressWarnings("unchecked")
            private void setExtraDataEditor(BillableItem value, boolean populate) {

                if (this.contains(proto().extraData())) {
                    this.unbind(proto().extraData());
                    extraDataPanel.setWidget(null);
                }

                if (value != null) {
                    @SuppressWarnings("rawtypes")
                    CEntityForm editor = null;
                    BillableItemExtraData extraData = value.extraData();

                    if (extraData.getInstanceValueClass() == YardiLeaseChargeData.class) {
                        editor = new YardiDataEditor();
                    } else {
                        if (ARCode.Type.features().contains(value.item().product().holder().code().type().getValue())) {
                            switch (value.item().product().holder().code().type().getValue()) {
                            case Parking:
                                editor = new VehicleDataEditor();
                                if (extraData.getInstanceValueClass() != Vehicle.class) {
                                    extraData.set(EntityFactory.create(Vehicle.class));
                                }
                                break;
                            case Pet:
                                editor = new PetDataEditor();
                                if (extraData.getInstanceValueClass() != Pet.class) {
                                    extraData.set(EntityFactory.create(Pet.class));
                                }
                                break;
                            default:
                                // ok - there is no extra-data for other types!.. 
                            }
                        }
                    }

                    if (editor != null) {
                        editor.setEditable(true);
                        editor.inheritEditable(false);
                        this.inject(proto().extraData(), editor);
                        editor.populate(extraData.cast());
                        extraDataPanel.setWidget(editor);
                    }
                }
            }
        }
    }

    private abstract static class SelectFeatureBox extends EntitySelectorListDialog<ProductItem> {

        public SelectFeatureBox(ARCode.Type type, UnitOptionsSelectionDTO selectionData) {
            super(i18n.tr("Select {0}(s)", type), true, getAvailableList(type, selectionData));
        }

        private static List<ProductItem> getAvailableList(ARCode.Type type, UnitOptionsSelectionDTO selectionData) {
            List<ProductItem> available = null;

            switch (type) {
            case AddOn:
            case Utility:
                available = selectionData.availableUtilities();
                break;
            case Pet:
                available = selectionData.availablePets();
                break;
            case Parking:
                available = selectionData.availableParking();
                break;
            case Locker:
                available = selectionData.availableStorage();
                break;
            default:
                available = selectionData.availableOther();
                break;
            }

            return available;
        }
    }
}
