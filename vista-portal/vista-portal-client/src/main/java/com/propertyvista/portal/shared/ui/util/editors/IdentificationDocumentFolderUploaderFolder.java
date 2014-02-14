/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util.editors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.domain.media.IdentificationDocumentFile;
import com.propertyvista.portal.rpc.portal.prospect.services.IdentificationDocumentProspectUploadService;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class IdentificationDocumentFolderUploaderFolder extends PortalBoxFolder<IdentificationDocumentFile> {

    private static final I18n i18n = I18n.get(IdentificationDocumentFolderUploaderFolder.class);

    public IdentificationDocumentFolderUploaderFolder() {
        super(IdentificationDocumentFile.class, i18n.tr("File"));
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof IdentificationDocumentFile) {
            return new ApplicationDocumentEditor();
        }
        return super.create(member);
    }

    private class ApplicationDocumentEditor extends CEntityForm<IdentificationDocumentFile> {

        public ApplicationDocumentEditor() {
            super(IdentificationDocumentFile.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();
            int row = -1;

            CFile cfile = new CFile(GWT.<UploadService<?, ?>> create(IdentificationDocumentProspectUploadService.class), new VistaFileURLBuilder(
                    IdentificationDocumentFile.class));

            main.setWidget(++row, 0, 1, new FormWidgetDecoratorBuilder(inject(proto().file(), cfile)).customLabel("").labelWidth("0px").build());

            return main;
        }
    }
}
