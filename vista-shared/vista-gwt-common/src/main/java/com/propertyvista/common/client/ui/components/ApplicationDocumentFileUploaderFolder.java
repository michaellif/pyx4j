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
package com.propertyvista.common.client.ui.components;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.media.ApplicationDocumentFile;

public class ApplicationDocumentFileUploaderFolder extends VistaTableFolder<ApplicationDocumentFile> {

    private static final I18n i18n = I18n.get(ApplicationDocumentFileUploaderFolder.class);

    private static final List<EntityFolderColumnDescriptor> COLUMNS;

    static {
        ApplicationDocumentFile proto = EntityFactory.getEntityPrototype(ApplicationDocumentFile.class);
        COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.fileName(), "20em"));
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.fileSize(), "5em"));
    }

    public ApplicationDocumentFileUploaderFolder() {
        super(ApplicationDocumentFile.class);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ApplicationDocumentFile) {
            return new ApplicationDocumentEditor();
        }
        return super.create(member);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return COLUMNS;
    }

    @Override
    protected void addItem() {

        new ApplicationDocumentUploaderDialog(i18n.tr("Upload Document")) {
            @Override
            protected void onUploadComplete(UploadResponse<IEntity> serverUploadResponse) {
                ApplicationDocumentFile docPage = EntityFactory.create(ApplicationDocumentFile.class);
                docPage.blobKey().setValue(serverUploadResponse.uploadKey);
                docPage.fileName().setValue(serverUploadResponse.fileName);
                docPage.fileSize().setValue(serverUploadResponse.fileSize);
                docPage.contentMimeType().setValue(serverUploadResponse.fileContentType);
                addItem(docPage);
            }
        }.show();
    }

    private class ApplicationDocumentEditor extends CEntityFolderRowEditor<ApplicationDocumentFile> {
        public ApplicationDocumentEditor() {
            super(ApplicationDocumentFile.class, columns());
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().fileName()) {
                if (isEditable()) {
                    CComponent<?, ?> comp = inject(column.getObject());
                    comp.setViewable(true);
                    return comp;
                } else {
                    CHyperlink link = new DownloadDocumentHyperlink() {
                        @Override
                        protected ApplicationDocumentFile getApplicationDocument() {
                            return ApplicationDocumentEditor.this.getValue();
                        }
                    };
                    return inject(column.getObject(), link);
                }
            } else {
                CComponent<?, ?> comp = inject(column.getObject());
                comp.setViewable(true);
                return comp;
            }
        }
    }

    private abstract class DownloadDocumentHyperlink extends CHyperlink {

        public DownloadDocumentHyperlink() {
            super(null);
            setCommand(new Command() {
                @Override
                public void execute() {
                    Window.open(MediaUtils.createApplicationDocumentUrl(getApplicationDocument()), "_blank", null);
                }
            });

            setFormat(new IFormat<String>() {

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
        }

        protected abstract ApplicationDocumentFile getApplicationDocument();

    }
}
