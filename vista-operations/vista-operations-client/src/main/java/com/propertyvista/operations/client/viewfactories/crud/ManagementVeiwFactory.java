/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.viewfactories.crud;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.operations.client.ui.crud.encryptedstorage.EncryptedStorageView;
import com.propertyvista.operations.client.ui.crud.encryptedstorage.EncryptedStorageViewImpl;
import com.propertyvista.operations.client.ui.crud.pmc.EquifaxApprovalView;
import com.propertyvista.operations.client.ui.crud.pmc.EquifaxApprovalViewImpl;
import com.propertyvista.operations.client.ui.crud.pmc.MerchantAccountEditorView;
import com.propertyvista.operations.client.ui.crud.pmc.MerchantAccountEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.pmc.MerchantAccountViewerView;
import com.propertyvista.operations.client.ui.crud.pmc.MerchantAccountViewerViewImpl;
import com.propertyvista.operations.client.ui.crud.pmc.PmcEditorView;
import com.propertyvista.operations.client.ui.crud.pmc.PmcEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.pmc.PmcListerView;
import com.propertyvista.operations.client.ui.crud.pmc.PmcListerViewImpl;
import com.propertyvista.operations.client.ui.crud.pmc.PmcViewerView;
import com.propertyvista.operations.client.ui.crud.pmc.PmcViewerViewImpl;
import com.propertyvista.operations.client.ui.crud.scheduler.run.RunDataListerView;
import com.propertyvista.operations.client.ui.crud.scheduler.run.RunDataListerViewImpl;
import com.propertyvista.operations.client.ui.crud.scheduler.run.RunDataViewerView;
import com.propertyvista.operations.client.ui.crud.scheduler.run.RunDataViewerViewImpl;
import com.propertyvista.operations.client.ui.crud.scheduler.run.RunListerView;
import com.propertyvista.operations.client.ui.crud.scheduler.run.RunListerViewImpl;
import com.propertyvista.operations.client.ui.crud.scheduler.run.RunViewerView;
import com.propertyvista.operations.client.ui.crud.scheduler.run.RunViewerViewImpl;
import com.propertyvista.operations.client.ui.crud.scheduler.trigger.TriggerEditorView;
import com.propertyvista.operations.client.ui.crud.scheduler.trigger.TriggerEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.scheduler.trigger.TriggerListerView;
import com.propertyvista.operations.client.ui.crud.scheduler.trigger.TriggerListerViewImpl;
import com.propertyvista.operations.client.ui.crud.scheduler.trigger.TriggerViewerView;
import com.propertyvista.operations.client.ui.crud.scheduler.trigger.TriggerViewerViewImpl;

public class ManagementVeiwFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IPane> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (PmcListerView.class.equals(type)) {
                map.put(type, new PmcListerViewImpl());
            } else if (PmcViewerView.class.equals(type)) {
                map.put(type, new PmcViewerViewImpl());
            } else if (PmcEditorView.class.equals(type)) {
                map.put(type, new PmcEditorViewImpl());

            } else if (EquifaxApprovalView.class.equals(type)) {
                map.put(type, new EquifaxApprovalViewImpl());

            } else if (TriggerViewerView.class.equals(type)) {
                map.put(type, new TriggerViewerViewImpl());
            } else if (TriggerEditorView.class.equals(type)) {
                map.put(type, new TriggerEditorViewImpl());
            } else if (TriggerListerView.class.equals(type)) {
                map.put(type, new TriggerListerViewImpl());

            } else if (RunViewerView.class.equals(type)) {
                map.put(type, new RunViewerViewImpl());
            } else if (RunListerView.class.equals(type)) {
                map.put(type, new RunListerViewImpl());

            } else if (RunDataViewerView.class.equals(type)) {
                map.put(type, new RunDataViewerViewImpl());

            } else if (RunDataListerView.class.equals(type)) {
                map.put(type, new RunDataListerViewImpl());

            } else if (MerchantAccountEditorView.class.equals(type)) {
                map.put(type, new MerchantAccountEditorViewImpl());
            } else if (MerchantAccountViewerView.class.equals(type)) {
                map.put(type, new MerchantAccountViewerViewImpl());

            } else if (EncryptedStorageView.class.equals(type)) {
                map.put(type, new EncryptedStorageViewImpl());
            }

        }

        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }
}
