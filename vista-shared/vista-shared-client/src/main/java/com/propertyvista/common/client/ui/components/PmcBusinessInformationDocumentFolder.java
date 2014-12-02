/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-23
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.pmc.info.PmcBusinessInfoDocument;

public class PmcBusinessInformationDocumentFolder extends VistaBoxFolder<PmcBusinessInfoDocument> {

    private static final I18n i18n = I18n.get(PmcBusinessInformationDocumentFolder.class);

    private class PmcBusinessInformationDocumentForm extends CForm<PmcBusinessInfoDocument> {

        public PmcBusinessInformationDocumentForm() {
            super(PmcBusinessInfoDocument.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            formPanel.append(Location.Left, proto().type()).decorate();
            if (isEditable()) {
                formPanel.h4(i18n.tr("Click 'Add' to upload document pages"));
            }
            formPanel.append(Location.Dual, proto().documentPages(), new PmcDocumentFileFolder());
            return formPanel;
        }

    }

    public PmcBusinessInformationDocumentFolder() {
        super(PmcBusinessInfoDocument.class);
        setAddable(false);
        setRemovable(false);
        setOrderable(false);
    }

    @Override
    protected CForm<PmcBusinessInfoDocument> createItemForm(IObject<?> member) {
        return new PmcBusinessInformationDocumentForm();
    }

    @Override
    public VistaBoxFolderItemDecorator<PmcBusinessInfoDocument> createItemDecorator() {
        VistaBoxFolderItemDecorator<PmcBusinessInfoDocument> d = super.createItemDecorator();
        d.setCollapsible(false);
        return d;
    }

}
