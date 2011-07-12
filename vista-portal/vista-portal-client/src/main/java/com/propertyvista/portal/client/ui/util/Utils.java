/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 10, 2011
 * @author dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.util;

import com.google.gwt.user.client.ui.Image;

import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.domain.payment.PaymentType;

public class Utils {

    public static Image getPaymentCardImage(PaymentType type) {
        String url;

        if (type == PaymentType.Echeck) {
            url = PortalImages.INSTANCE.paymentACH().getURL();
        } else if (type == PaymentType.Visa) {
            url = PortalImages.INSTANCE.paymentVISA().getURL();
        } else if (type == PaymentType.Amex) {
            url = PortalImages.INSTANCE.paymentAMEX().getURL();
        } else if (type == PaymentType.MasterCard) {
            url = PortalImages.INSTANCE.paymentMC().getURL();
        } else if (type == PaymentType.Discover) {
            url = PortalImages.INSTANCE.paymentDiscover().getURL();
        } else {
            url = PortalImages.INSTANCE.paymentInterac().getURL();
        }
        return new Image(url);
    }
}
