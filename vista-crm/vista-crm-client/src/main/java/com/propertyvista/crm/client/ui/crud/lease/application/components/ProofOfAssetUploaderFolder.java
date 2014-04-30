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
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.media.ProofOfAssetDocumentFolder;

public class ProofOfAssetUploaderFolder extends VistaBoxFolder<ProofOfAssetDocumentFolder> {

    private final static I18n i18n = I18n.get(ProofOfAssetUploaderFolder.class);

    public ProofOfAssetUploaderFolder() {
        super(ProofOfAssetDocumentFolder.class, i18n.tr("Proof Of Asset"));
    }

    @Override
    protected CForm<ProofOfAssetDocumentFolder> createItemForm(IObject<?> member) {
        return new ProofOfAssetDocumentEditor();
    }

    private class ProofOfAssetDocumentEditor extends CForm<ProofOfAssetDocumentFolder> {

        public ProofOfAssetDocumentEditor() {
            super(ProofOfAssetDocumentFolder.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicCFormPanel formPanel = new BasicCFormPanel(this);

            formPanel.append(Location.Dual, proto().description()).decorate();

            formPanel.h3(i18n.tr("Files"));
            formPanel.append(Location.Dual, proto().files(), new ProofOfAssetDocumentFileFolder());

            return formPanel;
        }
    }
}
