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

import java.text.ParseException;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class ApplicationDocumentFileUploaderFolder extends PortalBoxFolder<ApplicationDocumentFile> {

    private static final I18n i18n = I18n.get(ApplicationDocumentFileUploaderFolder.class);

    public ApplicationDocumentFileUploaderFolder() {
        super(ApplicationDocumentFile.class, i18n.tr("Document"));
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof ApplicationDocumentFile) {
            return new ApplicationDocumentEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        new ApplicationDocumentUploaderDialog(i18n.tr("Upload Document")) {
            @Override
            protected void onUploadComplete(ApplicationDocumentFile serverUploadResponse) {
                addItem(serverUploadResponse);
            }
        }.show();
    }

    private class ApplicationDocumentEditor extends CEntityForm<ApplicationDocumentFile> {

        public ApplicationDocumentEditor() {
            super(ApplicationDocumentFile.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();
            int row = -1;

            CTextField fileComp = new CTextField();
            fileComp.setViewable(true);
            fileComp.setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    Window.open(MediaUtils.createApplicationDocumentUrl(ApplicationDocumentEditor.this.getValue()), "_blank", null);
                }
            });
            fileComp.setFormat(new IFormat<String>() {
                @Override
                public String format(String value) {
                    if (value == null || value.equals("")) {
                        return i18n.tr("No File");
                    } else {
                        return value;
                    }
                }

                @Override
                public String parse(String string) throws ParseException {
                    return string;
                }
            });

            main.setWidget(++row, 0, 1, inject(proto().fileName(), fileComp));

            return main;
        }
    }
}
