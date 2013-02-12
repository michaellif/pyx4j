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
package com.propertyvista.operations.client.activity.crud.equifaxencryptedstorage;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.operations.client.ui.crud.equifaxencryptedstorage.EquifaxEncryptedStorageView;
import com.propertyvista.operations.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;
import com.propertyvista.operations.rpc.services.EncryptedStorageService;

public class EquifaxEncryptedStorageActivity extends AbstractActivity implements EquifaxEncryptedStorageView.Presenter {

    private final EquifaxEncryptedStorageView view;

    private final EncryptedStorageService service;

    public EquifaxEncryptedStorageActivity(AppPlace place) {
        view = ManagementVeiwFactory.instance(EquifaxEncryptedStorageView.class);
        service = GWT.<EncryptedStorageService> create(EncryptedStorageService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
        populate();

    }

    @Override
    public void createNewKey(char[] keyPassword) {

    }

    @Override
    public void makeCurrentKey(EncryptedStorageKeyDTO keyToActivate) {

    }

    @Override
    public void decryptionEnable(EncryptedStorageKeyDTO keyToEnableDecryption, char[] password) {

    }

    @Override
    public void downloadPrivateKey(EncryptedStorageKeyDTO key) {

    }

    @Override
    @Deprecated
    public void refresh() {
        // TODO 
    }

    @Override
    public void populate() {
        service.getSystemState(new DefaultAsyncCallback<EncryptedStorageDTO>() {
            @Override
            public void onSuccess(EncryptedStorageDTO result) {
                view.populate(result);
            }
        });
    }
}
