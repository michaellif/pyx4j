/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.themes;

import com.pyx4j.commons.css.Style;

import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;
import com.propertyvista.portal.shared.themes.PortalTheme;

public class ProspectPortalTheme extends PortalTheme {

    public ProspectPortalTheme() {
        super();
    }

    @Override
    protected void initStyles() {
        addTheme(new RentalSummaryTheme());
        addTheme(new SummaryStepTheme());
        addTheme(new ApplicationWizardTheme());

        addTheme(new PortalRootPaneTheme() {
            @Override
            protected void initMainMenuStyles() {
                super.initMainMenuStyles();

                Style style = new Style(".", StyleName.MainMenuNavigItem);
                style.addProperty("line-height", "30px");
                style.addProperty("height", "30px");
                style.addProperty("padding", "4px");
                addStyle(style);
            }
        });

        super.initStyles();
    }

}
