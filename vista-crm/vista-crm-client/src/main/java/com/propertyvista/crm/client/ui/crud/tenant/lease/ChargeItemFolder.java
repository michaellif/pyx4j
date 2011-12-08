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
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.VistaBoxFolder;
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
            new SelectFeatureBox() {
                @Override
                public boolean onClickOk() {
                    for (ServiceItem item : getSelectedItems()) {
                        ChargeItem newItem = EntityFactory.create(ChargeItem.class);
                        newItem.item().set(item);
                        newItem.originalPrice().setValue(item.price().getValue());
                        newItem.adjustedPrice().setValue(item.price().getValue());
                        addItem(newItem);
                    }
                    return true;
                }
            }.show();
        }
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ChargeItem) {
            return new ChargeItemEditor(this);
        }
        return super.create(member);
    }

    private abstract class SelectFeatureBox extends OkCancelDialog {

        private ListBox list;

        public SelectFeatureBox() {
            super(i18n.tr("Select Features"));
            setBody(createBody());
            setSize("300px", "100px");
        }

        protected Widget createBody() {
            getOkButton().setEnabled(false);

            if (!parent.getValue().selectedFeatureItems().isEmpty()) {
                list = new ListBox(true);
                list.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        getOkButton().setEnabled(list.getSelectedIndex() >= 0);
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

        protected List<ServiceItem> getSelectedItems() {
            List<ServiceItem> selectedItems = new ArrayList<ServiceItem>(4);
            for (int i = 0; i < list.getItemCount(); ++i) {
                if (list.isItemSelected(i)) {
                    for (ServiceItem item : parent.getValue().selectedFeatureItems()) {
                        if (list.getValue(i).contentEquals(item.id().toString())) {
                            selectedItems.add(item);
                        }
                    }
                }
            }
            return selectedItems;
        }
    }
}