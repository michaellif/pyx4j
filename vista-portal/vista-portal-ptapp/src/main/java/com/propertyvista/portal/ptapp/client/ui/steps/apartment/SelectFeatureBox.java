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

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;

abstract class SelectFeatureBox extends EntitySelectorListDialog<ProductItem> {

    private static final I18n i18n = I18n.get(OkCancelDialog.class);

    public SelectFeatureBox(Feature.Type type, ApartmentInfoDTO apartmentInfo) {
        super(i18n.tr("Select {0}(s)", type), true, getAvailableList(type, apartmentInfo));
    }

    @Override
    public String defineWidth() {
        return "300px";
    }

    @Override
    public String defineHeight() {
        return "100px";
    }

    private static List<ProductItem> getAvailableList(Feature.Type type, ApartmentInfoDTO apartmentInfo) {
        // TODO in the previous version there was a commented out part of code that restricted addition of already selected items (from apartmentInfo.agreed*())
        List<ProductItem> available = null;
        switch (type) {
        case utility:
            available = apartmentInfo.availableUtilities();
            break;
        case pet:
            available = apartmentInfo.availablePets();
            break;
        case parking:
            available = apartmentInfo.availableParking();
            break;
        case locker:
            available = apartmentInfo.availableStorage();
            break;
        default:
            available = apartmentInfo.availableOther();
            break;
        }
        List<ProductItem> result = new ArrayList<ProductItem>(available.size());
        for (ProductItem item : available) {
            if (isCompatible(item, type)) {
                result.add(item);
            }
        }
        return result;
    }

    private static boolean isCompatible(ProductItem item, Feature.Type type) {

        if (type.equals(Feature.Type.addOn)) {
            switch (item.type().<FeatureItemType> cast().featureType().getValue()) {
            case utility:
            case pet:
            case parking:
            case locker:
                return false;

            default:
                return true;
            }
        }
        return (item.type().<FeatureItemType> cast().featureType().getValue().equals(type));
    }
}