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
package com.propertyvista.portal.ptapp.client.ui.steps.apartment;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.lease.BillableItem;

public class FeatureFolder extends VistaTableFolder<BillableItem> {

    private static final I18n i18n = I18n.get(FeatureFolder.class);

    private final ARCode.Type type;

    private final ApartmentViewForm apartmentViewForm;

    private int maxCount = -1;

    public FeatureFolder(ARCode.Type type, ApartmentViewForm apartmentViewForm, boolean modifiable) {
        super(BillableItem.class, modifiable);

        this.type = type;
        this.apartmentViewForm = apartmentViewForm;
    }

    protected int getMaxCount() {
        return maxCount;
    };

    protected void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().item().code(), "13em"));
        columns.add(new EntityFolderColumnDescriptor(proto().agreedPrice(), "7em"));
        columns.add(new EntityFolderColumnDescriptor(proto().item().description(), "50em"));
        return columns;

    }

    @Override
    protected void addItem() {
        if (apartmentViewForm != null && getMaxCount() >= 0) {
            if (getValue().size() < getMaxCount()) {
                new SelectFeatureBox(type, apartmentViewForm.getValue()) {
                    @Override
                    public boolean onClickOk() {
                        for (ProductItem item : getSelectedItems()) {
                            if (getValue().size() < getMaxCount()) {
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
    }

    protected void unconditionalRemoveItem(CEntityFolderItem<BillableItem> item) {
        super.removeItem(item);
    }

    @Override
    protected void removeItem(final CEntityFolderItem<BillableItem> item) {
        if (!item.getValue().adjustments().isEmpty()) {
            MessageDialog.confirm(i18n.tr("Warning!"),
                    i18n.tr("By removing the selected item you will lose the agreed price and adjustment(s)! Are you sure you want to remove it?"),
                    new Command() {
                        @Override
                        public void execute() {
                            unconditionalRemoveItem(item);
                        }
                    });
        } else {
            super.removeItem(item);
        }
    }

}