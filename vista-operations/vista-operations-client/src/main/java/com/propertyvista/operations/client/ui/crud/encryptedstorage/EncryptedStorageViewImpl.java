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

import com.pyx4j.site.client.ui.ViewImplBase;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;

public class EncryptedStorageViewImpl extends ViewImplBase implements EncryptedStorageView {

    private final EnryptedStorageForm form;

    private final Button newKeyButton;

    private com.propertyvista.operations.client.ui.crud.encryptedstorage.EncryptedStorageView.Presenter presenter;

    public EncryptedStorageViewImpl() {
        form = new EnryptedStorageForm();
        form.initContent();

        setContentPane(form);
        setSize("100%", "100%");

        newKeyButton = new Button("Add new Key Pair", new Command() {

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
        addHeaderToolbarItem(newKeyButton);

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
