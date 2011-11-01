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



import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceItem;

class FeatureExFolder extends VistaBoxFolder<ChargeItem> {

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