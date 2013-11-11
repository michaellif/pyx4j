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
package com.propertyvista.operations.client.activity.crud.encryptedstorage;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.PopupPanel;

import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.PasswordSerializable;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.components.DownloadLinkDialog;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.encryptedstorage.EncryptedStorageView;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;
import com.propertyvista.operations.rpc.services.EncryptedStorageService;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class EncryptedStorageActivity extends AbstractActivity implements EncryptedStorageView.Presenter {

    private final AppPlace place;

    private final EncryptedStorageView view;

    private final EncryptedStorageService service;

    public EncryptedStorageActivity(AppPlace place) {
        this.place = place;
        view = OperationsSite.getViewFactory().getView(EncryptedStorageView.class);
        service = GWT.<EncryptedStorageService> create(EncryptedStorageService.class);
    }

    @Override
    public AppPlace getPlace() {
        return place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
        populate();

    }

    @Override
    public void refresh() {
        populate();
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

    @Override
    public void createNewKey(char[] keyPassword) {
        service.createNewKeyPair(new DefaultAsyncCallback<String>() {

            @Override
            public void onSuccess(String fileName) {
                DownloadLinkDialog dd = new DownloadLinkDialog("Download Encrypted Private Key");
                dd.addCloseHandler(new CloseHandler<PopupPanel>() {
                    @Override
                    public void onClose(CloseEvent<PopupPanel> event) {
                        refresh();
                    }
                });
                dd.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
                dd.show(//@formatter:off
                        "",
                        "Encrypted Private Key",
                        fileName
                );//@formatter:on

            }
        }, new PasswordSerializable(keyPassword));
    }

    @Override
    public void makeCurrentKey(EncryptedStorageKeyDTO key) {
        service.makeCurrent(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                refresh();
            }

        }, key.getPrimaryKey());
    }

    @Override
    public void activateDecryption(EncryptedStorageKeyDTO keyToEnableDecryption, char[] password) {
        service.activateDecryption(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                refresh();
            }

        }, keyToEnableDecryption.getPrimaryKey(), new PasswordSerializable(password));
    }

    @Override
    public void disableDecryption(EncryptedStorageKeyDTO keyToDisableDecryption) {
        service.deactivateDecryption(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                refresh();
            }

        }, keyToDisableDecryption.getPrimaryKey());

    }

    @Override
    public void startKeyRotation(EncryptedStorageKeyDTO key) {

        service.startKeyRotation(new DefaultAsyncCallback<String>() {

            @Override
            public void onSuccess(String deferredCorrelationId) {
                DeferredProcessDialog d = new DeferredProcessDialog("Key Rotation", "Re encrypting Data to current key...", false) {
                    @Override
                    public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                        super.onDeferredSuccess(result);
                        refresh();
                    }
                };
                d.show();
                d.startProgress(deferredCorrelationId);
            }
        }, key.getPrimaryKey());
    }

    @Override
    public void activateCurrentKeyDecryption(char[] password) {
        service.activateCurrentKeyDecryption(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                refresh();
            }
        }, new PasswordSerializable(password));
    }

    @Override
    public void deactivateDecryption() {
        service.deactivateDecryption(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                refresh();
            }
        });
    }
}
