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
package com.propertyvista.portal.resident.themes;

import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;
import com.propertyvista.portal.shared.themes.PortalTheme;

public class ResidentPortalTheme extends PortalTheme {

    public ResidentPortalTheme() {
        super();
    }

    @Override
    public void initStyles(Skin skin) {
        super.initStyles(skin);
        addTheme(new TenantSureTheme());
        addTheme(new ExtraGadgetsTheme());
        addTheme(new PortalRootPaneTheme());
        addTheme(new CommunicationTheme());
        addTheme(new MoveInWizardTheme());

    }

}
