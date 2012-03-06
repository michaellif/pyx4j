/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.dialogs.SelectDialog;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.dto.LeaseDTO;

class BillableItemFolder extends VistaBoxFolder<BillableItem> {

    private static final I18n i18n = I18n.get(BillableItemFolder.class);

    private final List<BillableItem> populatedValues = new LinkedList<BillableItem>();

    private final CEntityEditor<LeaseDTO> lease;

    public BillableItemFolder(boolean modifyable, CEntityEditor<LeaseDTO> lease) {
        super(BillableItem.class, modifyable);
        this.lease = lease;
    }

    @Override
    public IFolderItemDecorator<BillableItem> createItemDecorator() {
        BoxFolderItemDecorator<BillableItem> decor = (BoxFolderItemDecorator<BillableItem>) super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        // memorize populated values: 
        populatedValues.clear();
        populatedValues.addAll(getValue());
    }

    @Override
    protected void addItem() {
        if (lease.getValue().leaseProducts().serviceItem().isNull()) {
            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("You Must Select A Service Item First"));
        } else {
            new SelectDialog<ProductItem>(i18n.tr("Select Features"), true, lease.getValue().selectedFeatureItems()) {
                @Override
                public boolean onClickOk() {
                    for (ProductItem item : getSelectedItems()) {
                        BillableItem newItem = EntityFactory.create(BillableItem.class);
                        newItem.item().set(item);
                        newItem._currentPrice().setValue(item.price().getValue());
                        newItem.effectiveDate().setValue(new LogicalDate());
                        addItem(newItem);
                    }
                    return true;
                }

                @Override
                public String defineWidth() {
                    return "400px";
                }

                @Override
                public String defineHeight() {
                    return "200px";
                }
            }.show();
        }
    }

    @Override
    protected void removeItem(CEntityFolderItem<BillableItem> item) {
        if (!lease.getValue().approvalDate().isNull() && populatedValues.contains(item.getValue())) {
            item.getValue().expirationDate().setValue(new LogicalDate());
            item.setValue(item.getValue(), false);
            item.setEditable(false);
            ValueChangeEvent.fire(this, getValue());
        } else {
            super.removeItem(item);
        }
    }

    @Override
    protected CEntityFolderItem<BillableItem> createItem(boolean first) {
        final CEntityFolderItem<BillableItem> item = super.createItem(first);
        item.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyName.repopulated) {
                    if (isModifiable() && !lease.getValue().approvalDate().isNull()) {
                        LogicalDate value = item.getValue().expirationDate().getValue();
                        if ((value != null) && !value.after(TimeUtils.today())) {
                            item.setViewable(true);
                            item.inheritViewable(false);

                            item.setMovable(false);
                            item.setRemovable(false);

                            // compensate the fact that item.setViewable DOESN'T call kids' setViewable!?
                            for (CComponent<?, ?> comp : item.getComponents()) {
                                comp.setViewable(true);
                            }
                        }
                    }
                }
            }
        });
        return item;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof BillableItem) {
            return new BillableItemEditor(lease);
        }
        return super.create(member);
    }
}