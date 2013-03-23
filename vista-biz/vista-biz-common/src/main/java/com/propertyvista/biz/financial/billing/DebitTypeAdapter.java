/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class DebitTypeAdapter {

    public DebitType getDebitType(ProductItemType productItemType) {
        DebitType debitType = null;

        if (productItemType == null) {
            debitType = DebitType.other;
        } else if (productItemType.isInstanceOf(ServiceItemType.class)) {
            debitType = DebitType.lease;
        } else if (productItemType.isInstanceOf(FeatureItemType.class)) {
            switch (productItemType.<FeatureItemType> cast().featureType().getValue()) {
            case parking:
                debitType = DebitType.parking;
                break;
            case pet:
                debitType = DebitType.pet;
                break;
            case addOn:
                debitType = DebitType.addOn;
                break;
            case utility:
                debitType = DebitType.utility;
                break;
            case locker:
                debitType = DebitType.locker;
                break;
            case booking:
                debitType = DebitType.booking;
                break;
            default:
                debitType = DebitType.other;
                break;
            }
        }
        return debitType;
    }
}
