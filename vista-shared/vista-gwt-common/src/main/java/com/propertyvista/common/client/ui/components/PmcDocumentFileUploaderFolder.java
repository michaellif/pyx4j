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

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.pmc.info.PmcDocumentFile;

public class PmcDocumentFileUploaderFolder extends VistaTableFolder<PmcDocumentFile> {

    private static final I18n i18n = I18n.get(PmcDocumentFileUploaderFolder.class);

    public static final List<EntityFolderColumnDescriptor> COLUMNS;
    static {
        PmcDocumentFile proto = EntityFactory.getEntityPrototype(PmcDocumentFile.class);
        COLUMNS = Arrays.asList((new EntityFolderColumnDescriptor(proto.fileName(), "30em")));
    }

    private static class CDownloadPmcDocumentHyperlink extends CHyperlink<String> {

        public CDownloadPmcDocumentHyperlink() {
            super("");
            setCommand(new Command() {
                @Override
                public void execute() {
                    // TODO actually file download                    
                    // Window.open(MediaUtils.createApplicationDocumentUrl(getApplicationDocument()), "_blank", null);
                    Window.alert("TODO implement file download");
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
    }

    private static class PmcDocumentFileForm extends CEntityFolderRowEditor<PmcDocumentFile> {

        public PmcDocumentFileForm() {
            super(PmcDocumentFile.class, COLUMNS);
            setViewable(true);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().fileName()) {
                CComponent<?, ?> cmp = isEditable() ? new CTextField() : new CDownloadPmcDocumentHyperlink();
                inject(column.getObject(), cmp);
                return cmp;
            }
            return super.createCell(column);
        }
    }

    private final Collection<DownloadFormat> supportedFormats;

    private final UploadService<IEntity, IEntity> uploadService;

    public PmcDocumentFileUploaderFolder(UploadService<IEntity, IEntity> uploadService, Collection<DownloadFormat> supportedFormats) {
        super(PmcDocumentFile.class);
        setOrderable(false);
        this.uploadService = uploadService;
        this.supportedFormats = supportedFormats;
    }

    @Override
    protected void addItem() {
        new PmcDocumentFileUploaderDialog(uploadService, supportedFormats) {

            @Override
            protected void onUploadComplete(UploadResponse<IEntity> serverUploadResponse) {

                PmcDocumentFile docPage = EntityFactory.create(PmcDocumentFile.class);
                docPage.blobKey().setValue(serverUploadResponse.uploadKey);
                docPage.fileName().setValue(serverUploadResponse.fileName);
                docPage.fileSize().setValue(serverUploadResponse.fileSize);
                docPage.contentMimeType().setValue(serverUploadResponse.fileContentType);
                addItem(docPage);

            }
        }.show();

    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return COLUMNS;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PmcDocumentFile) {
            return new PmcDocumentFileForm();
        }
        return super.create(member);
    }

}
