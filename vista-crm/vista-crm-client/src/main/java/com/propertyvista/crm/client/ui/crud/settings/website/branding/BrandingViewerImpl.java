/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 5, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.website.branding;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.dto.SiteDescriptorDTO;

public class BrandingViewerImpl extends CrmViewerViewImplBase<SiteDescriptorDTO> implements BrandingViewer {

    private static final I18n i18n = I18n.get(BrandingViewerImpl.class);

    public BrandingViewerImpl() {
        setForm(new BrandingForm(this));
    }
}