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

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceItem;

class FeatureFolder extends VistaTableFolder<ChargeItem> {

    private final Feature.Type type;

    private final ApartmentViewForm apartmentViewForm;

    public FeatureFolder(Feature.Type type, ApartmentViewForm apartmentViewForm, boolean modifyable) {
        super(ChargeItem.class, modifyable);
        this.type = type;
        this.apartmentViewForm = apartmentViewForm;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
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
                    i18n.tr("By removing the selected item you will lose the agreed price adjustment! Are you sure you want to remove it?"), new Command() {

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