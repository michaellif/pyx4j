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
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.media.ProofOfAssetDocumentFolder;

public class ProofOfAssetUploaderFolder extends VistaBoxFolder<ProofOfAssetDocumentFolder> {

    private final static I18n i18n = I18n.get(ProofOfAssetUploaderFolder.class);

    public ProofOfAssetUploaderFolder() {
        super(ProofOfAssetDocumentFolder.class, i18n.tr("Proof Of Asset"));
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof ProofOfAssetDocumentFolder) {
            return new ProofOfAssetDocumentEditor();
        }
        return super.create(member);
    }

    private class ProofOfAssetDocumentEditor extends CEntityForm<ProofOfAssetDocumentFolder> {

        public ProofOfAssetDocumentEditor() {
            super(ProofOfAssetDocumentFolder.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().description()), 50, true).build());

            content.setH3(++row, 0, 2, i18n.tr("Files"));
            content.setWidget(++row, 0, 2, inject(proto().files(), new ProofOfAssetDocumentFileFolder()));

            return content;
        }
    }
}
