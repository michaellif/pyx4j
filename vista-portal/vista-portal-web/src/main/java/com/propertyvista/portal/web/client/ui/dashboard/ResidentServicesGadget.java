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
package com.propertyvista.portal.web.client.ui.dashboard;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.dto.ResidentServicesDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;

public class ResidentServicesGadget extends AbstractGadget<MainDashboardViewImpl> {

    static final I18n i18n = I18n.get(ResidentServicesGadget.class);

    ResidentServicesGadget(MainDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Resident Services"), ThemeColor.contrast3);
        setActionsToolbar(new ResidentServicesToolbar());
        FlowPanel contentPanel = new FlowPanel();
        contentPanel
                .add(new HTML(
                        "<b>Our records indicate you do not have valid tenant insurance.</b><br>As per your lease agreement, you must obtain and provide the landlord with proof of tenant insurance."));

        setContent(contentPanel);
    }

    protected void populate(ResidentServicesDTO value) {

    }

    class ResidentServicesToolbar extends Toolbar {
        public ResidentServicesToolbar() {

            Button purchaseButton = new Button("Purchase Insurance");
            purchaseButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 1));
            add(purchaseButton);

            Button proofButton = new Button("Provide Proof of my Insurance");
            proofButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 0.6));
            add(proofButton);

        }
    }

}
