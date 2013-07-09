/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.insurance;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;

public class InsuranceUploaderFolder extends VistaBoxFolder<InsuranceCertificate> {

    private final static I18n i18n = I18n.get(InsuranceUploaderFolder.class);

    public InsuranceUploaderFolder() {
        super(InsuranceCertificate.class, false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof InsuranceCertificate) {
            return new InsuranceCertificateDocumentEditor();
        } else {
            return super.create(member);
        }
    }

    private static class InsuranceCertificateDocumentEditor extends CEntityDecoratableForm<InsuranceCertificate> {

        public InsuranceCertificateDocumentEditor() {
            super(InsuranceCertificate.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();
            content.setSize("100%", "100%");
            int row = -1;
            content.setH3(++row, 0, 1, i18n.tr("Files"));
            //content.setWidget(++row, 0, inject(proto().documentPages(), new ApplicationDocumentFileUploaderFolder()));
            return content;
        }

    }
}
