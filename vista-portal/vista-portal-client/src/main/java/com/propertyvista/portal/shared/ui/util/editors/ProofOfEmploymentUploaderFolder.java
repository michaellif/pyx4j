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
package com.propertyvista.portal.shared.ui.util.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.media.ProofOfEmploymentDocument;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class ProofOfEmploymentUploaderFolder extends CEntityFolder<ProofOfEmploymentDocument> {

    private final static I18n i18n = I18n.get(ProofOfEmploymentUploaderFolder.class);

    public ProofOfEmploymentUploaderFolder() {
        super(ProofOfEmploymentDocument.class);
    }

    @Override
    public IFolderItemDecorator<ProofOfEmploymentDocument> createItemDecorator() {
        BoxFolderItemDecorator<ProofOfEmploymentDocument> decor = new BoxFolderItemDecorator<ProofOfEmploymentDocument>(VistaImages.INSTANCE);
        return decor;
    }

    @Override
    protected IFolderDecorator<ProofOfEmploymentDocument> createFolderDecorator() {
        return new BoxFolderDecorator<ProofOfEmploymentDocument>(VistaImages.INSTANCE, "Add Proof Of Employment");
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
            BasicFlexFormPanel content = new BasicFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().description()), 250).build());

            content.setH3(++row, 0, 1, i18n.tr("Files"));
            content.setWidget(++row, 0, inject(proto().documentPages(), new ApplicationDocumentFileUploaderFolder()));

            return content;
        }
    }
}
