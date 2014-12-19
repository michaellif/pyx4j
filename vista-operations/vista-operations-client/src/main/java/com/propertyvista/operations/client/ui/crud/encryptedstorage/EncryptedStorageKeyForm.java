/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-12
 * @author ArtyomB
 */
package com.propertyvista.operations.client.ui.crud.encryptedstorage;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.gwt.client.upload.UploadReceiver;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.operations.client.ui.crud.encryptedstorage.EncryptedStorageView.Presenter;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;

public class EncryptedStorageKeyForm extends CForm<EncryptedStorageKeyDTO> {

    private static final I18n i18n = I18n.get(EncryptedStorageKeyForm.class);

    private Presenter presenter;

    private Button makeCurrent;

    private Button startKeyRotation;

    private Button decryptOnOff;

    private Button uploadEncryptedPrivateKey;

    public EncryptedStorageKeyForm() {
        super(EncryptedStorageKeyDTO.class);
        setViewable(true);
        setEditable(false);
    }

    @Override
    protected IsWidget createContent() {
        FlowPanel contentPanel = new FlowPanel();

        contentPanel.add(makeStatusPanel());
        contentPanel.add(makeControlPanel());

        return contentPanel;
    }

    public void setPresenter(EncryptedStorageView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        makeCurrent.setEnabled(!getValue().isCurrent().getValue(false));
        decryptOnOff.setCaption(!getValue().decryptionEnabled().getValue(false) ? i18n.tr("Activate Decryption") : i18n.tr("Disable Decryption"));
        startKeyRotation.setEnabled(!getValue().isCurrent().getValue(false) && getValue().decryptionEnabled().getValue(false)
                && getValue().recordsCount().getValue() > 0);
    }

    private IsWidget makeStatusPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().name()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().isCurrent()).decorate().componentWidth(60);
        formPanel.append(Location.Left, proto().decryptionEnabled()).decorate().componentWidth(60);

        formPanel.append(Location.Right, proto().recordsCount()).decorate();
        formPanel.append(Location.Right, proto().created()).decorate();
        formPanel.append(Location.Right, proto().expired()).decorate();

        formPanel.append(Location.Left, proto().details()).decorate();

        return formPanel;
    }

    private Widget makeControlPanel() {
        FlowPanel controlPanel = new FlowPanel();

        makeCurrent = new Button(i18n.tr("Set as CURRENT"), new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Please confirm..."), i18n.tr("Set this key pair as current?"), new Command() {
                    @Override
                    public void execute() {
                        presenter.makeCurrentKey(EncryptedStorageKeyForm.this.getValue());
                    }
                });

            }

        });

        makeCurrent.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        controlPanel.add(makeCurrent);

        decryptOnOff = new Button("", new Command() {
            @Override
            public void execute() {
                if (!getValue().decryptionEnabled().getValue(false)) {
                    new PasswordEntryDialog(false) {
                        @Override
                        public boolean onClickOk() {
                            if (getPassword() != null) {
                                presenter.activateDecryption(EncryptedStorageKeyForm.this.getValue(), getPassword());
                                return true;
                            } else {
                                return false;
                            }

                        }
                    }.show();
                } else {
                    presenter.disableDecryption(EncryptedStorageKeyForm.this.getValue());
                }
            }

        });

        decryptOnOff.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        controlPanel.add(decryptOnOff);

        startKeyRotation = new Button(i18n.tr("Start Key Rotation"), new Command() {
            @Override
            public void execute() {
                presenter.startKeyRotation(EncryptedStorageKeyForm.this.getValue());
            }
        });

        startKeyRotation.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        controlPanel.add(startKeyRotation);

        uploadEncryptedPrivateKey = new Button(i18n.tr("Upload Encrypted Private Key"), new Command() {

            @Override
            public void execute() {
                upload();
            }

        });

        uploadEncryptedPrivateKey.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        controlPanel.add(uploadEncryptedPrivateKey);

        return controlPanel;
    }

    private void upload() {
        UploadReceiver uploadReciver = new UploadReceiver() {

            @Override
            public void onUploadComplete(IFile<?> uploadResponse) {
                presenter.refresh();
            }
        };
        new PrivateKeyFileUploadDialog(this.getValue(), uploadReciver).show();
    }
}
