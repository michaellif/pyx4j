/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.services.insurance;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.GeneralInsuranceCertificateDTO;
import com.propertyvista.portal.web.client.ui.CPortalEntityEditor;

public class GeneralCertificatePage extends CPortalEntityEditor<GeneralInsuranceCertificateDTO> {

    private final static I18n i18n = I18n.get(GeneralCertificatePage.class);

    public GeneralCertificatePage(GeneralCertificatePageView view) {
        super(GeneralInsuranceCertificateDTO.class, view, i18n.tr("Insurance Certificated"), ThemeColor.contrast3);
    }

    @Override
    public IsWidget createContent() {
        return null;
    }

}