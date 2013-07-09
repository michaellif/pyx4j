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

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.operations.client.ui.crud.encryptedstorage.EncryptedStorageView.Presenter;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;

public class EnryptedStorageKeyFolder extends VistaBoxFolder<EncryptedStorageKeyDTO> {

    private Presenter presenter;

    public EnryptedStorageKeyFolder() {
        super(EncryptedStorageKeyDTO.class);
        setAddable(false);
        setViewable(true);
        setEditable(false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof EncryptedStorageKeyDTO) {
            EncryptedStorageKeyForm form = new EncryptedStorageKeyForm();
            form.setPresenter(presenter);
            return form;
        }
        return super.create(member);
    }

    public void setPresenter(EncryptedStorageView.Presenter presenter) {
        this.presenter = presenter;
    }
}
