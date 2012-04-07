/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.editors.PetDataEditor;
import com.propertyvista.common.client.ui.components.editors.VehicleDataEditor;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemExtraData;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;

class BillableItemEditor extends CEntityDecoratableEditor<BillableItem> {

    private static final I18n i18n = I18n.get(BillableItemEditor.class);

    private final SimplePanel extraDataPanel = new SimplePanel();

    private final FormFlexPanel adjustmentPanel = new FormFlexPanel();

    private final LeaseEditorForm form;

    private boolean isService = false;

    public BillableItemEditor(LeaseEditorForm form, boolean isService) {
        this(form);
        this.isService = isService;
    }

    public BillableItemEditor(LeaseEditorForm form) {
        super(BillableItem.class);
        this.form = form;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().item(), new CEntitySelectorHyperlink<ProductItem>() {
            @Override
            protected AppPlace getTargetPlace() {
                //TODO make it working
                if (false) {
                    return AppPlaceEntityMapper.resolvePlace(ProductItem.class, getValue().product().getPrimaryKey());
                } else {
                    return null;
                }
            }

            @Override
            protected EntitySelectorListDialog<ProductItem> getSelectorDialog() {
                return new EntitySelectorListDialog<ProductItem>(i18n.tr("Service Item Selection"), false, form.getValue().selectedServiceItems()) {
                    @Override
                    public boolean onClickOk() {
                        List<ProductItem> selectedItems = getSelectedItems();
                        if (!selectedItems.isEmpty()) {
                            ((LeaseEditorView.Presenter) ((LeaseEditorView) form.getParentView()).getPresenter()).setSelectedService(selectedItems.get(0));
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public String defineHeight() {
                        return "100px";
                    };

                    @Override
                    public String defineWidth() {
                        return "400px";
                    }

                    @Override
                    public void show() {
                        if (form.getValue().selectedBuilding() == null || form.getValue().selectedBuilding().isNull()) {
                            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("You Must Select Unit First"));
                        } else {
                            super.show();
                        }
                    }
                };

            }
        }), 20).build());

        get(proto().item()).setViewable(!isService);

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().effectiveDate()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().expirationDate()), 9).build());

        main.setWidget(++row, 0, extraDataPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, adjustmentPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        adjustmentPanel.setH3(0, 0, 1, i18n.tr("Adjustments"));
        adjustmentPanel.setWidget(1, 0, inject(proto().adjustments(), new AdjustmentFolder()));

        get(proto().effectiveDate()).setVisible(false);
        get(proto().expirationDate()).setVisible(false);

        return main;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void onPopulate() {
        super.onPopulate();

        if (!getValue().item().isEmpty()) {
            if (getValue().item().type().isInstanceOf(FeatureItemType.class)) {
                // show effective dates:
                get(proto().effectiveDate()).setVisible(true);
                get(proto().expirationDate()).setVisible(true);

                if (getValue().item().type().<FeatureItemType> cast().featureType().getValue() == Feature.Type.utility) {
                    // correct folder item:
                    if (getParent() instanceof CEntityFolderItem) {
                        CEntityFolderItem<BillableItem> item = (CEntityFolderItem<BillableItem>) getParent();
                        item.setRemovable(false);
                    }
                }
            } else if (getValue().item().type().isInstanceOf(ServiceItemType.class)) {
                // hide effective dates:
                get(proto().effectiveDate()).setVisible(false);
                get(proto().expirationDate()).setVisible(false);
            }

            if (isViewable()) {
                adjustmentPanel.setVisible(!getValue().adjustments().isEmpty());
            } else {
                adjustmentPanel.setVisible(true);
            }

            CEntityEditor editor = null;
            BillableItemExtraData extraData = getValue().extraData();

            // add extraData editor if necessary:
            if (getValue().item().type().isInstanceOf(FeatureItemType.class)) {
                switch (getValue().item().type().<FeatureItemType> cast().featureType().getValue()) {
                case parking:
                    editor = new VehicleDataEditor();
                    if (extraData.isNull()) {
                        extraData.set(EntityFactory.create(Vehicle.class));
                    }
                    break;
                case pet:
                    editor = new PetDataEditor();
                    if (extraData.isNull()) {
                        extraData.set(EntityFactory.create(Pet.class));
                    }
                    break;
                }
            }

            if (editor != null) {
                if (this.contains(proto().extraData())) {
                    this.unbind(proto().extraData());
                }
                this.inject(proto().extraData(), editor);
                editor.populate(extraData.cast());
                extraDataPanel.setWidget(editor);
            }
        } else {// tweak UI for empty ProductItem:  
            adjustmentPanel.setVisible(false);
        }

        if (isEditable()) {
            get(proto().item()).setViewable(false);
            if (isService) {
                get(proto().item()).setEditable(form.getValue().approvalDate().isNull());
            } else {
                get(proto().item()).setEditable(false);
            }
        } else {
            get(proto().item()).setViewable(true);
        }
    }

    @Override
    public void applyViewabilityRules() {
        super.applyViewabilityRules();
        if (adjustmentPanel != null && getValue() != null) {
            if (isViewable() && !getValue().isNull()) {
                adjustmentPanel.setVisible(!getValue().adjustments().isEmpty());
            } else {
                adjustmentPanel.setVisible(true);
            }
        }
    }

    private class AdjustmentFolder extends VistaTableFolder<BillableItemAdjustment> {

        private final List<BillableItemAdjustment> populatedValues = new LinkedList<BillableItemAdjustment>();

        public AdjustmentFolder() {
            super(BillableItemAdjustment.class, !BillableItemEditor.this.isViewable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().adjustmentType(), "8em"));
            columns.add(new EntityFolderColumnDescriptor(proto().chargeType(), "9em"));
            columns.add(new EntityFolderColumnDescriptor(proto().executionType(), "8em"));
            columns.add(new EntityFolderColumnDescriptor(proto().value(), "5em"));
            columns.add(new EntityFolderColumnDescriptor(proto().effectiveDate(), "9em"));
            columns.add(new EntityFolderColumnDescriptor(proto().expirationDate(), "9em"));
            columns.add(new EntityFolderColumnDescriptor(proto().createdBy(), "15em"));
            return columns;
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            CComponent<?, ?> comp = super.create(member);
            if (member instanceof Employee) {
                comp.setViewable(true);
            }
            return comp;
        }

        @Override
        protected void onPopulate() {
            super.onPopulate();

            // memorize populated values: 
            populatedValues.clear();
            populatedValues.addAll(getValue());
        }

        @Override
        protected void addItem(BillableItemAdjustment newEntity) {
            if (newEntity.isEmpty()) {
                newEntity.effectiveDate().setValue(new LogicalDate());
            }
            super.addItem(newEntity);
        }

        @Override
        protected void removeItem(CEntityFolderItem<BillableItemAdjustment> item) {
            if (!form.getValue().approvalDate().isNull() && populatedValues.contains(item.getValue())) {
                item.getValue().expirationDate().setValue(new LogicalDate());
                item.setValue(item.getValue(), false);
                item.setEditable(false);
                ValueChangeEvent.fire(this, getValue());
            } else {
                super.removeItem(item);
            }
        }

        @Override
        protected CEntityFolderItem<BillableItemAdjustment> createItem(boolean first) {
            final CEntityFolderItem<BillableItemAdjustment> item = super.createItem(first);
            item.addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent event) {
                    if (event.getPropertyName() == PropertyName.repopulated) {
                        if (isAddable() && !form.getValue().approvalDate().isNull()) {
                            LogicalDate value = item.getValue().expirationDate().getValue();
                            if ((value != null) && !value.after(TimeUtils.today())) {
                                item.setViewable(true);
                                item.inheritViewable(false);

                                item.setMovable(false);
                                item.setRemovable(false);
                            }
                        }
                    }
                }
            });
            return item;
        }
    }
}