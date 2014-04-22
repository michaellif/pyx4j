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
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.encryptedstorage;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.client.upload.UploadReceiver;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
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
        Widget statusPanel = makeStatusPanel();
        statusPanel.getElement().getStyle().setDisplay(Display.BLOCK);
        statusPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        statusPanel.getElement().getStyle().setWidth(40, Unit.EM);

        Widget controlPanel = makeControlPanel();
        controlPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        controlPanel.getElement().getStyle().setFloat(Style.Float.RIGHT);
        contentPanel.add(statusPanel);
        contentPanel.add(controlPanel);

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
        startKeyRotation.setEnabled(!getValue().isCurrent().getValue(false) && getValue().decryptionEnabled().getValue(false));
    }

    private Widget makeStatusPanel() {
        TwoColumnFlexFormPanel statusPanel = new TwoColumnFlexFormPanel();
        int row = -1;
        statusPanel.setWidget(++row, 0, inject(proto().name(), new FieldDecoratorBuilder().componentWidth(15).build()));
        statusPanel.setWidget(++row, 0, inject(proto().isCurrent(), new FieldDecoratorBuilder().componentWidth(5).build()));
        statusPanel.setWidget(++row, 0, inject(proto().decryptionEnabled(), new FieldDecoratorBuilder().componentWidth(5).build()));

        row = -1;
        statusPanel.setWidget(++row, 1, inject(proto().recordsCount(), new FieldDecoratorBuilder().build()));
        statusPanel.setWidget(++row, 1, inject(proto().created(), new FieldDecoratorBuilder().build()));
        statusPanel.setWidget(++row, 1, inject(proto().expired(), new FieldDecoratorBuilder().build()));
        return statusPanel;
    }

    private Widget makeControlPanel() {
        FlowPanel controlPanel = new FlowPanel();
        controlPanel.getElement().getStyle().setTextAlign(TextAlign.LEFT);

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
        controlPanel.add(new SimplePanel(makeCurrent));

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
        controlPanel.add(new SimplePanel(decryptOnOff));

        startKeyRotation = new Button(i18n.tr("Start Key Rotation"), new Command() {
            @Override
            public void execute() {
                presenter.startKeyRotation(EncryptedStorageKeyForm.this.getValue());
            }
        });
        controlPanel.add(new SimplePanel(startKeyRotation));

        uploadEncryptedPrivateKey = new Button(i18n.tr("Upload Encrypted Private Key"), new Command() {

            @Override
            public void execute() {
                upload();
            }

        });
        controlPanel.add(new SimplePanel(uploadEncryptedPrivateKey));

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
