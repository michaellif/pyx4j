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
package com.propertyvista.portal.prospect.ui.application.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.media.ProofOfAssetDocumentFolder;
import com.propertyvista.portal.shared.ui.PortalFormPanel;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class ProofOfAssetUploaderFolder extends PortalBoxFolder<ProofOfAssetDocumentFolder> {

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
            PortalFormPanel formPanel = new PortalFormPanel(this);
            formPanel.append(Location.Left, proto().description()).decorate().componentWidth(250);

            formPanel.h3(i18n.tr("Files"));
            formPanel.append(Location.Left, proto().files(), new ProofOfAssetDocumentFileFolder());

            return formPanel;
        }
    }
}
