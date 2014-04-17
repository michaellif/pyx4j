/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-31
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.ItemActionsBar.ActionType;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.operations.client.resources.OperationsImages;
import com.propertyvista.operations.rpc.services.PmcCrudService;

public class YardiCredentialFolder extends VistaBoxFolder<PmcYardiCredential> {

    private static final I18n i18n = I18n.get(YardiCredentialFolder.class);

    public YardiCredentialFolder() {
        super(PmcYardiCredential.class);
    }

    @Override
    public <T extends CComponent<T, ?>> T create(IObject<?> member) {
        if (member instanceof PmcYardiCredential) {
            return (T) new YardiCredentialEditor();
        }
        return super.create(member);
    }

    @Override
    protected CEntityFolderItem<PmcYardiCredential> createItem(boolean first) {
        final CEntityFolderItem<PmcYardiCredential> item = super.createItem(first);

        item.addAction(ActionType.Cust1, i18n.tr("Test Connection"), OperationsImages.INSTANCE.connectionTestButton(), new Command() {

            @Override
            public void execute() {
                GWT.<PmcCrudService> create(PmcCrudService.class).testYardiConnectionDeferred(new DefaultAsyncCallback<String>() {
                    @Override
                    public void onSuccess(String deferredCorrelationId) {
                        DeferredProcessDialog d = new DeferredProcessDialog(i18n.tr("Yardi Connection Test"), i18n.tr("Connecting..."), false);
                        d.show();
                        d.startProgress(deferredCorrelationId);
                    }
                }, item.getValue());
            }
        });
        return item;
    }
}
