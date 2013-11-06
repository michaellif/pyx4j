/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.offers.dashboard;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.resident.resources.PortalImages;
import com.propertyvista.portal.resident.ui.AbstractGadget;

public class OffersGadget extends AbstractGadget<OffersDashboardViewImpl> {

    private static final I18n i18n = I18n.get(OffersGadget.class);

    OffersGadget(OffersDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.offersIcon(), i18n.tr("Offers"), ThemeColor.contrast6, 1);

        setContent(new HTML("Perks & Offers Coming Soon!"));
    }

}
