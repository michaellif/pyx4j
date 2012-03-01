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
import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.editors.PetDataEditor;
import com.propertyvista.common.client.ui.components.editors.VehicleDataEditor;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemExtraData;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;

class BillableItemEditor extends CEntityDecoratableEditor<BillableItem> {

    private static final I18n i18n = I18n.get(BillableItemEditor.class);

    private final SimplePanel extraDataPanel = new SimplePanel();

    private final FormFlexPanel adjustmentPanel = new FormFlexPanel();

    public BillableItemEditor() {
        super(BillableItem.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().item().type().name(), new CLabel())).customLabel("").useLabelSemicolon(false).build());
        get(proto().item().type().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().item().price()), 6).build());
        get(proto().item().price()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
        get(proto().item().price()).setViewable(true);

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().effectiveDate()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().expirationDate()), 9).build());

        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().exemptFromTax()), 5).build());

        main.setWidget(++row, 0, extraDataPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, adjustmentPanel);
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        adjustmentPanel.setH3(0, 0, 1, i18n.tr("Adjustments"));
        adjustmentPanel.setWidget(1, 0, inject(proto().adjustments(), new ChargeItemAdjustmentFolder()));

        return main;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void onPopulate() {
        super.onPopulate();

        if (!getValue().item().isEmpty()) {
            switch (getValue().item().type().type().getValue()) {
            case feature:
                if (getValue().item().type().featureType().getValue() == Feature.Type.utility) {
                    // correct folder item:
                    if (getParent() instanceof CEntityFolderItem) {
                        CEntityFolderItem<BillableItem> item = (CEntityFolderItem<BillableItem>) getParent();
                        item.setRemovable(false);
                    }
                }
                break;
            case service:
                // hide effective dates:
                get(proto().effectiveDate()).setVisible(false);
                get(proto().expirationDate()).setVisible(false);
                break;
            }

            if (isViewable()) {
                adjustmentPanel.setVisible(!getValue().adjustments().isEmpty());
            } else {
                get(proto().exemptFromTax()).setVisible(true);
                adjustmentPanel.setVisible(true);
            }

            CEntityEditor editor = null;
            BillableItemExtraData extraData = getValue().extraData();

            // add extraData editor if necessary:
            switch (getValue().item().type().type().getValue()) {
            case feature:
                switch (getValue().item().type().featureType().getValue()) {
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
                break;
            }

            if (editor != null) {
                this.inject(proto().extraData(), editor);
                editor.populate(extraData.cast());
                extraDataPanel.setWidget(editor);
            }
        } else {// tweak UI for empty ProductItem:  
            get(proto().exemptFromTax()).setVisible(false);
            adjustmentPanel.setVisible(false);
        }
    }

    @Override
    public void setViewable(boolean viewable) {
        super.setViewable(viewable);
        if (viewable && !getValue().isNull()) {
            adjustmentPanel.setVisible(!getValue().adjustments().isEmpty());
        } else {
            adjustmentPanel.setVisible(true);
        }
    }

    private class ChargeItemAdjustmentFolder extends VistaTableFolder<BillableItemAdjustment> {

        public ChargeItemAdjustmentFolder() {
            super(BillableItemAdjustment.class, !BillableItemEditor.this.isViewable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().adjustmentType(), "8em"));
            columns.add(new EntityFolderColumnDescriptor(proto().chargeType(), "9em"));
            columns.add(new EntityFolderColumnDescriptor(proto().termType(), "8em"));
            columns.add(new EntityFolderColumnDescriptor(proto().value(), "5em"));
            columns.add(new EntityFolderColumnDescriptor(proto().effectiveDate(), "9em"));
            columns.add(new EntityFolderColumnDescriptor(proto().expirationDate(), "9em"));
            return columns;
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
            item.getValue().expirationDate().setValue(new LogicalDate());
            item.setValue(item.getValue(), false);
            item.setEditable(false);
            ValueChangeEvent.fire(this, getValue());
        }

        @Override
        protected CEntityFolderItem<BillableItemAdjustment> createItem(boolean first) {
            final CEntityFolderItem<BillableItemAdjustment> item = super.createItem(first);
            item.addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent event) {
                    if (event.getPropertyName() == PropertyName.repopulated) {
                        if (isModifiable()) {
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