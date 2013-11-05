/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.folders;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.ApplicationDocumentFileUploaderFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.media.ProofOfEmploymentDocument;

public class ProofOfEmploymentUploaderFolder extends VistaBoxFolder<ProofOfEmploymentDocument> {

    private final static I18n i18n = I18n.get(ProofOfEmploymentUploaderFolder.class);

    public ProofOfEmploymentUploaderFolder() {
        super(ProofOfEmploymentDocument.class);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof ProofOfEmploymentDocument) {
            return new ProofOfEmploymentDocumentEditor();
        }
        return super.create(member);
    }

    private class ProofOfEmploymentDocumentEditor extends CEntityForm<ProofOfEmploymentDocument> {

        public ProofOfEmploymentDocumentEditor() {
            super(ProofOfEmploymentDocument.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().description()), 50, true).build());
            content.setH3(++row, 0, 2, i18n.tr("Files"));
            content.setWidget(++row, 0, 2, inject(proto().documentPages(), new ApplicationDocumentFileUploaderFolder()));

            return content;
        }
    }
}
