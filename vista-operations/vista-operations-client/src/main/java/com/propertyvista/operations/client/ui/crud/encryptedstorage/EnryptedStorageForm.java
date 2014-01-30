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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;

import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;

public class EnryptedStorageForm extends CEntityForm<EncryptedStorageDTO> {

    private EnryptedStorageKeyFolder folder;

    public EnryptedStorageForm() {
        super(EncryptedStorageDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();
        panel.add(inject(proto().keys(), folder = new EnryptedStorageKeyFolder()));
        return panel;
    }

    public void setPresenter(EncryptedStorageView.Presenter presenter) {
        folder.setPresenter(presenter);
    }

}
