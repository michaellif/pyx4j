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
package com.propertyvista.operations.client.ui.crud.equifaxencryptedstorage;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.operations.client.ui.crud.equifaxencryptedstorage.EquifaxEncryptedStorageView.Presenter;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;

public class EquifaxEncryptedStorageKeyForm extends CEntityDecoratableForm<EncryptedStorageKeyDTO> {

    private static final I18n i18n = I18n.get(EquifaxEncryptedStorageKeyForm.class);

    private Presenter presenter;

    private Button makeCurrent;

    private Button downloadPrivateKey;

    private Button enableDecrypt;

    public EquifaxEncryptedStorageKeyForm() {
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
                presenter.makeCurrentKey(EquifaxEncryptedStorageKeyForm.this.getValue());
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
                            presenter.decryptionEnable(EquifaxEncryptedStorageKeyForm.this.getValue(), getPassword());
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            }

        });
        controlPanel.add(enableDecrypt);

        downloadPrivateKey = new Button(i18n.tr("Download Pr. Key"), new Command() {
            @Override
            public void execute() {
                presenter.downloadPrivateKey(EquifaxEncryptedStorageKeyForm.this.getValue());
            }
        });
        contentPanel.add(downloadPrivateKey);

        return statusPanel;
    }

    public void setPresenter(EquifaxEncryptedStorageView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        makeCurrent.setVisible(!getValue().isCurrent().isBooleanTrue());
        enableDecrypt.setVisible(!getValue().decryptionEnabled().isBooleanTrue());
        downloadPrivateKey.setVisible(true);
    }
}
