/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 13, 2014
 * @author arminea
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.portal.resident.resources.tenantsure.TenantSureResources;
import com.propertyvista.portal.shared.ui.AbstractGadget;

public class TenanatSureAboutGadget extends AbstractGadget<TenantSureAboutViewImpl> {

    public TenanatSureAboutGadget(TenantSureAboutViewImpl viewer, ThemeColor themeColor, double themeVibrance) {
        super(viewer, themeColor, themeVibrance);
        setContent(new HTML(TenantSureResources.INSTANCE.contactInfo().getText()));
    }

}
