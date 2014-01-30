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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.pmc.info.PmcBusinessInfoDocument;

public class PmcBusinessInformationDocumentFolder extends VistaBoxFolder<PmcBusinessInfoDocument> {

    private static final I18n i18n = I18n.get(PmcBusinessInformationDocumentFolder.class);

    private class PmcBusinessInformationDocumentForm extends CEntityForm<PmcBusinessInfoDocument> {

        public PmcBusinessInformationDocumentForm() {
            super(PmcBusinessInfoDocument.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
            int row = -1;
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().type())).labelWidth(5).build());
            if (isEditable()) {
                content.setH4(++row, 0, 1, i18n.tr("Click 'Add' to upload document pages"));
            }
            content.setWidget(++row, 0, inject(proto().documentPages(), new PmcDocumentFileFolder()));
            return content;
        }

    }

    public PmcBusinessInformationDocumentFolder() {
        super(PmcBusinessInfoDocument.class);
        setAddable(false);
        setRemovable(false);
        setOrderable(false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PmcBusinessInfoDocument) {
            return new PmcBusinessInformationDocumentForm();
        }
        return super.create(member);
    };

    @Override
    public IFolderItemDecorator<PmcBusinessInfoDocument> createItemDecorator() {
        VistaBoxFolderItemDecorator<PmcBusinessInfoDocument> d = (VistaBoxFolderItemDecorator<PmcBusinessInfoDocument>) super.createItemDecorator();
        d.setCollapsible(false);
        return d;
    }

}
