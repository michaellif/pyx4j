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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.operations.client.ui.crud.encryptedstorage.EncryptedStorageView.Presenter;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;

public class EncryptedStorageKeyForm extends CEntityDecoratableForm<EncryptedStorageKeyDTO> {

    private static final I18n i18n = I18n.get(EncryptedStorageKeyForm.class);

    private Presenter presenter;

    private Button makeCurrent;

    private Button startKeyRotation;

    private Button enableDecrypt;

    public EncryptedStorageKeyForm() {
        super(EncryptedStorageKeyDTO.class);
        setViewable(true);
        setEditable(false);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel contentPanel = new FlowPanel();

        FormFlexPanel statusPanel = new FormFlexPanel();
        int row = -1;
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().isCurrent())).build());
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().decryptionEnabled())).build());
        statusPanel.setWidget(++row, 0, new HTML("&nbsp"));
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().recordsCount())).build());
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().created(), new CDatePicker())).build());
        statusPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().expired(), new CDatePicker())).build());
        contentPanel.add(statusPanel);

        FlowPanel controlPanel = new FlowPanel();

        makeCurrent = new Button(i18n.tr("Make Current"), new Command() {
            @Override
            public void execute() {
                presenter.makeCurrentKey(EncryptedStorageKeyForm.this.getValue());
            }

        });
        controlPanel.add(makeCurrent);

        enableDecrypt = new Button(i18n.tr("Enable Decryption"), new Command() {
            @Override
            public void execute() {
                new PasswordEntryDialog() {
                    @Override
                    public boolean onClickOk() {
                        if (getPassword() != null) {
                            presenter.activateDecryption(EncryptedStorageKeyForm.this.getValue(), getPassword());
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            }

        });
        controlPanel.add(enableDecrypt);

        startKeyRotation = new Button(i18n.tr("Start Key Rotation"), new Command() {
            @Override
            public void execute() {
                presenter.startKeyRotation(EncryptedStorageKeyForm.this.getValue());
            }
        });
        contentPanel.add(startKeyRotation);

        return statusPanel;
    }

    public void setPresenter(EncryptedStorageView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        makeCurrent.setVisible(!getValue().isCurrent().isBooleanTrue());
        enableDecrypt.setVisible(!getValue().decryptionEnabled().isBooleanTrue());
        startKeyRotation.setVisible(true);
    }
}
