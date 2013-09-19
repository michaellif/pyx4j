/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.encryptedstorage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.client.upload.FileUploadDialog;
import com.pyx4j.gwt.client.upload.FileUploadReciver;
import com.pyx4j.gwt.client.upload.UploadPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.PasswordSerializable;

import com.propertyvista.operations.rpc.dto.PrivateKeyDTO;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;
import com.propertyvista.operations.rpc.services.EncryptedStorageServicePrivateKeyUploadService;

public class PrivateKeyFileUploadDialog extends FileUploadDialog<PrivateKeyDTO> {

    private static final I18n i18n = I18n.get(PrivateKeyFileUploadDialog.class);

    private final EncryptedStorageKeyDTO keyToUpload;

    private CEntityForm<PasswordEntryDTO> form;

    public PrivateKeyFileUploadDialog(EncryptedStorageKeyDTO keyToUpload, FileUploadReciver<PrivateKeyDTO> uploadReciver) {
        super(i18n.tr("Upload Private Key File"), null, GWT
                .<UploadService<PrivateKeyDTO, PrivateKeyDTO>> create(EncryptedStorageServicePrivateKeyUploadService.class), uploadReciver);

        this.keyToUpload = keyToUpload;
    }

    @Override
    protected IsWidget createContent(final UploadPanel<PrivateKeyDTO, PrivateKeyDTO> uploadPanel) {

        form = new CEntityForm<PasswordEntryDTO>(PasswordEntryDTO.class) {
            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, uploadPanel);
                main.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().password())).componentWidth(10).build());
                return main;
            }
        };

        form.initContent();
        form.populateNew();

        return form.asWidget();
    }

    @Override
    protected PrivateKeyDTO getUploadData() {
        PrivateKeyDTO data = EntityFactory.create(PrivateKeyDTO.class);
        data.password().setValue(new PasswordSerializable(form.getValue().password().getValue().toCharArray()));
        data.publicKeyKey().setValue(keyToUpload.getPrimaryKey());
        return data;
    }
}
