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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;

public abstract class ApplicationDocumentUploaderFolder extends VistaTableFolder<ApplicationDocument> {

    private static final I18n i18n = I18n.get(ApplicationDocumentUploaderFolder.class);

    public ApplicationDocumentUploaderFolder() {
        super(ApplicationDocument.class);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ApplicationDocument) {
            return new ApplicationDocumentEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        ApplicationDocument newDocument = EntityFactory.create(ApplicationDocument.class);
        // HACK: setting fileName as empty string in order to render its hyperlink as "Upload File" (hyperlink has optimization so it doesn't render anything if the value is null) 
        newDocument.fileName().setValue("");
        addItem(newDocument);
    }

    private class ApplicationDocumentEditor extends CEntityFolderRowEditor<ApplicationDocument> {
        public ApplicationDocumentEditor() {
            super(ApplicationDocument.class, columns());
            addValueValidator(new EditableValueValidator<ApplicationDocument>() {
                @Override
                public ValidationFailure isValid(CComponent<ApplicationDocument, ?> component, ApplicationDocument value) {
                    if (value != null && (value.fileName().isNull() | "".equals(value.fileName().getValue()))) {
                        return new ValidationFailure(i18n.tr("Please upload a document file"));
                    } else {
                        return null;
                    }
                }
            });
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().fileName()) {
                if (!isEditable()) {
                    CHyperlink link = new DownloadDocumentHyperlink() {
                        @Override
                        protected ApplicationDocument getApplicationDocument() {
                            return ApplicationDocumentEditor.this.getValue();
                        }
                    };
                    return inject(column.getObject(), link);
                } else {
                    CHyperlink link = new UploadDocumentHyperlink() {
                        @Override
                        protected void onUploadComlete(UploadResponse<IEntity> serverUploadResponse) {
                            ApplicationDocument doc = ApplicationDocumentEditor.this.getValue();
                            doc.id().setValue(null);
                            doc.blobKey().setValue(serverUploadResponse.uploadKey);
                            doc.fileName().setValue(serverUploadResponse.fileName);
                            doc.fileSize().setValue(serverUploadResponse.fileSize);
                            doc.contentMimeType().setValue(serverUploadResponse.fileContentType);
                            ApplicationDocumentEditor.this.setValue(doc);
                        }
                    };
                    return inject(column.getObject(), link);
                }

            } else if (column.getObject() == proto().fileSize()) {
                CComponent<?, ?> comp = inject(column.getObject());
                comp.setViewable(true);
                return comp;
            } else if (column.getObject() == proto().idType()) {
                @SuppressWarnings("unchecked")
                CEntityComboBox<IdentificationDocumentType> idCombo = (CEntityComboBox<IdentificationDocumentType>) inject(column.getObject(),
                        new CEntityComboBox<IdentificationDocumentType>(IdentificationDocumentType.class));

                // TODO fix this
//                idCombo.setOptionsDataSource(new EntityDataSource<IdentificationDocumentType>() {
//                    @Override
//                    public void obtain(EntityQueryCriteria<IdentificationDocumentType> criteria,
//                            final AsyncCallback<EntitySearchResult<IdentificationDocumentType>> handlingCallback) {
//                        ClientPolicyManager.obtainEffectivePolicy(ClientPolicyManager.getOrganizationPoliciesNode(), ApplicationDocumentationPolicy.class,
//                                new DefaultAsyncCallback<ApplicationDocumentationPolicy>() {
//                                    @Override
//                                    public void onSuccess(ApplicationDocumentationPolicy result) {
//                                        EntitySearchResult<IdentificationDocumentType> allowedIds = new EntitySearchResult<IdentificationDocumentType>();
//                                        allowedIds.getData().addAll(result.allowedIDs());
//                                        handlingCallback.onSuccess(allowedIds);
//                                    }
//                                });
//                    }
//                });

                return idCombo;
            } else {
                return super.createCell(column);
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

        protected abstract ApplicationDocument getApplicationDocument();

    }

    private abstract class UploadDocumentHyperlink extends CHyperlink {

        public UploadDocumentHyperlink() {
            super(null);
            setCommand(new Command() {

                @Override
                public void execute() {
                    new ApplicationDocumentUploaderDialog(i18n.tr("Upload Document")) {
                        @Override
                        protected void onUploadComplete(UploadResponse<IEntity> serverUploadResponse) {
                            UploadDocumentHyperlink.this.onUploadComlete(serverUploadResponse);
                        }
                    }.show();
                }
            });
            setFormat(new IFormat<String>() {
                @Override
                public String parse(String string) throws ParseException {
                    return string;
                }

                @Override
                public String format(String fileName) {
                    if (fileName == null) {
                        return i18n.tr("Upload File");
                    } else {

                        return i18n.tr("{0} (Upload New File)", fileName);
                    }
                }
            });
        }

        protected abstract void onUploadComlete(UploadResponse<IEntity> serverUploadResponse);

    }

}
