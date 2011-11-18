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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.dto.LeaseDTO;

class ChargeItemFolder extends VistaBoxFolder<ChargeItem> {

    private static I18n i18n = I18n.get(ChargeItemFolder.class);

    final CrmEntityForm<LeaseDTO> parent;

    public ChargeItemFolder(boolean modifyable, CrmEntityForm<LeaseDTO> parent) {
        super(ChargeItem.class, modifyable);
        this.parent = parent;
    }

    @Override
    protected void addItem() {
        if (parent.getValue().serviceAgreement().serviceItem().isNull()) {
            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("You Must Select A Service Item First"));
        } else {
            new ShowPopUpBox<SelectFeatureBox>(new SelectFeatureBox()) {
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

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ChargeItem) {
            return new ChargeItemEditor(this);
        }
        return super.create(member);
    }

    private class SelectFeatureBox extends OkCancelBox {

        private ListBox list;

        private List<ServiceItem> selectedItems;

        public SelectFeatureBox() {
            super(i18n.tr("Select Features"));
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);

            if (!parent.getValue().selectedFeatureItems().isEmpty()) {
                list = new ListBox(true);
                list.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        okButton.setEnabled(list.getSelectedIndex() >= 0);
                    }
                });

//                    List<ServiceItem> alreadySelected = new ArrayList<ServiceItem>();
//                    for (ChargeItem item : getValue().serviceAgreement().featureItems()) {
//                        alreadySelected.add(item.item());
//                    }

                for (ServiceItem item : parent.getValue().selectedFeatureItems()) {
//  TODO not sure if we need duplicate item check here:
//                        if (!alreadySelected.contains(item)) {
                    list.addItem(item.getStringView());
                    list.setValue(list.getItemCount() - 1, item.id().toString());
//                        }
                }

                if (list.getItemCount() > 0) {
                    list.setVisibleItemCount(8);
                    list.setWidth("100%");
                    return list.asWidget();
                } else {
                    return new HTML(i18n.tr("All Features have already been selected!"));
                }
            } else {
                return new HTML(i18n.tr("There Are No Features For This Service"));
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
                    for (ServiceItem item : parent.getValue().selectedFeatureItems()) {
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
    }
}