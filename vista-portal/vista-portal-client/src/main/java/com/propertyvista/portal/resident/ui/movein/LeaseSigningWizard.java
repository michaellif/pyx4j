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
package com.propertyvista.portal.resident.ui.movein;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.resident.dto.movein.LeaseAgreementDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;

public class LeaseSigningWizard extends CPortalEntityWizard<LeaseAgreementDTO> {

    private final static I18n i18n = I18n.get(LeaseSigningWizard.class);

    public LeaseSigningWizard(LeaseSigningWizardView view) {
        super(LeaseAgreementDTO.class, view, i18n.tr("Lease"), i18n.tr("Submit"), ThemeColor.contrast2);

        addStep(createDetailsStep());
        addStep(createAgreementStep());
    }

    public BasicFlexFormPanel createDetailsStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Lease Details"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        return panel;
    }

    public BasicFlexFormPanel createAgreementStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Lease Agreement"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setWidget(++row, 0, inject(proto().legalTerms(), new LegalTermsFolder()));

        return panel;
    }
}