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

import com.pyx4j.site.client.ui.prime.AbstractPrimePane;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;

public class EncryptedStorageViewImpl extends AbstractPrimePane implements EncryptedStorageView {

    private final EnryptedStorageForm form;

    private final Button createNewKeyButton;

    private com.propertyvista.operations.client.ui.crud.encryptedstorage.EncryptedStorageView.Presenter presenter;

    private final Button activateCurrentKeyForDecyptionButton;

    private Button deactivateDecryption;

    public EncryptedStorageViewImpl() {
        form = new EnryptedStorageForm();
        form.init();

        setContentPane(form);
        setSize("100%", "100%");

        activateCurrentKeyForDecyptionButton = new Button("Activate Current Key Decryption", new Command() {

            @Override
            public void execute() {
                new PasswordEntryDialog(false) {

                    @Override
                    public boolean onClickOk() {
                        if (getPassword() != null) {
                            presenter.activateCurrentKeyDecryption(getPassword());
                            return true;
                        } else {
                            return false;
                        }
                    }
                }.show();
            }
        });
        addHeaderToolbarItem(activateCurrentKeyForDecyptionButton);

        deactivateDecryption = new Button("Deactivate Decryption", new Command() {

            @Override
            public void execute() {
                MessageDialog.confirm("Are you sure?", "Please confirm you want to deactivate decription", new Command() {
                    @Override
                    public void execute() {
                        presenter.deactivateDecryption();
                    }
                });

            }

        });
        addHeaderToolbarItem(deactivateDecryption);

        createNewKeyButton = new Button("Add New Key Pair", new Command() {

            @Override
            public void execute() {
                new PasswordEntryDialog() {

                    @Override
                    public boolean onClickOk() {
                        if (getPassword() != null) {
                            presenter.createNewKey(getPassword());
                            return true;
                        } else {
                            return false;
                        }
                    }
                }.show();

            }

        });
        addHeaderToolbarItem(createNewKeyButton);

    }

    @Override
    public void setPresenter(EncryptedStorageView.Presenter presenter) {
        this.presenter = presenter;
        form.setPresenter(presenter);
    }

    @Override
    public void populate(EncryptedStorageDTO dto) {
        form.populate(dto);
    }

}
