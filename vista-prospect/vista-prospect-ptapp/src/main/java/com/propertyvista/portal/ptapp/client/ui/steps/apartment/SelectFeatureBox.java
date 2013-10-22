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

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;

abstract class SelectFeatureBox extends EntitySelectorListDialog<ProductItem> {

    private static final I18n i18n = I18n.get(OkCancelDialog.class);

    public SelectFeatureBox(ARCode.Type type, ApartmentInfoDTO apartmentInfo) {
        super(i18n.tr("Select {0}(s)", type), true, getAvailableList(type, apartmentInfo));
    }

    @Override
    public int defineWidth() {
        return 500;
    }

    @Override
    public String defineHeight() {
        return "100px";
    }

    private static List<ProductItem> getAvailableList(ARCode.Type type, ApartmentInfoDTO apartmentInfo) {
        // TODO in the previous version there was a commented out part of code that restricted addition of already selected items (from apartmentInfo.agreed*())
        List<ProductItem> available = null;
        switch (type) {
        case Utility:
            available = apartmentInfo.availableUtilities();
            break;
        case Pet:
            available = apartmentInfo.availablePets();
            break;
        case Parking:
            available = apartmentInfo.availableParking();
            break;
        case Locker:
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

    private static boolean isCompatible(ProductItem item, ARCode.Type type) {
        if (type.equals(ARCode.Type.AddOn)) {
            switch (item.code().type().getValue()) {
            case Utility:
            case Pet:
            case Parking:
            case Locker:
                return false;

            default:
                return true;
            }
        }
        return (item.code().type().getValue().equals(type));
    }
}