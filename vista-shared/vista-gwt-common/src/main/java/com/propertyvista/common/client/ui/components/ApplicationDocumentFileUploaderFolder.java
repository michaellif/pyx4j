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
import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.media.ApplicationDocumentFile;

public class ApplicationDocumentFileUploaderFolder extends VistaTableFolder<ApplicationDocumentFile> {

    private static final I18n i18n = I18n.get(ApplicationDocumentFileUploaderFolder.class);

    public ApplicationDocumentFileUploaderFolder() {
        super(ApplicationDocumentFile.class, i18n.tr("File"));
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList((new EntityFolderColumnDescriptor(proto().fileName(), "30em")));
    }

    @Override
    protected IFolderDecorator<ApplicationDocumentFile> createFolderDecorator() {
        TableFolderDecorator<ApplicationDocumentFile> folderDecorator = (TableFolderDecorator<ApplicationDocumentFile>) super.createFolderDecorator();
        folderDecorator.setShowHeader(false);
        return folderDecorator;
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
            protected void onUploadComplete(UploadResponse<IFile> serverUploadResponse) {
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
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().fileName()) {
                CTextField comp = inject(column.getObject(), new CTextField());
                comp.setViewable(true);
                comp.setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        Window.open(MediaUtils.createApplicationDocumentUrl(ApplicationDocumentEditor.this.getValue()), "_blank", null);
                    }
                });
                comp.setFormat(new IFormat<String>() {
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
                return comp;

            } else {
                CComponent<?> comp = inject(column.getObject());
                comp.setViewable(true);
                return comp;
            }
        }
    }
}
