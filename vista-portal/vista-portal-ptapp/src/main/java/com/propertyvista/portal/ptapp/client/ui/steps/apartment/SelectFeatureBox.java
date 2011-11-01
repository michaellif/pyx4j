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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;

class SelectFeatureBox extends OkCancelBox {

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