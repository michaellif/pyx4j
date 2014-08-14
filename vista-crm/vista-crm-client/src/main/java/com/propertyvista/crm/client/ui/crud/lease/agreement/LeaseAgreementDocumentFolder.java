/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-02-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.agreement;

import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

public class LeaseAgreementDocumentFolder extends VistaBoxFolder<LeaseTermAgreementDocument> {

    private static final I18n i18n = I18n.get(LeaseAgreementDocumentFolder.class);

    private List<LeaseTermParticipant<?>> participantOptions;

    private CrmUser uploader;

    public LeaseAgreementDocumentFolder() {
        super(LeaseTermAgreementDocument.class);
        setEditable(true);
        setAddable(true);
        setOrderable(false);
    }

    @Override
    protected CForm<LeaseTermAgreementDocument> createItemForm(IObject<?> member) {
        return new LeaseAgreementDocumentForm(true);
    }

    @Override
    protected void addItem() {
        new LeaseAgreementDocumentUploadDialog() {
            @Override
            public void accept(LeaseTermAgreementDocument document) {
                LeaseAgreementDocumentFolder.this.addItem(document);
            }
        }.show();
    }

    @Override
    protected void addItem(LeaseTermAgreementDocument newEntity) {
        super.addItem(newEntity);
        onDocumentsChanged();
    }

    public void onDocumentsChanged() {

    }

    @Override
    protected void removeItem(CFolderItem<LeaseTermAgreementDocument> item) {
        super.removeItem(item);
        onDocumentsChanged();
    }

    public void setParticipantOptions(List<LeaseTermParticipant<?>> participantOptions) {
        this.participantOptions = participantOptions;
    }

    public void setUploaderEmployee(CrmUser uploader) {
        this.uploader = uploader;
    }

    public abstract class LeaseAgreementDocumentUploadDialog extends OkCancelDialog {

        private final LeaseAgreementDocumentForm form;

        public LeaseAgreementDocumentUploadDialog() {
            super(i18n.tr("Upload Agreement Document"));
            form = new LeaseAgreementDocumentForm(false);
            form.init();

            LeaseTermAgreementDocument newDoc = EntityFactory.create(LeaseTermAgreementDocument.class);
            newDoc.signedEmployeeUploader().set(uploader);
            form.populate(newDoc);
            form.setParticipantOptions(LeaseAgreementDocumentFolder.this.participantOptions);
            setBody(form);
            setDialogPixelWidth(500);
        }

        @Override
        public boolean onClickOk() {
            form.setVisitedRecursive();
            if (form.isValid()) {
                accept(form.getValue());
                return true;
            }
            return false;
        }

        public abstract void accept(LeaseTermAgreementDocument document);
    }
}
