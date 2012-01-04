/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LeaseTermsPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LeaseTermsPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LeaseTermsPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.leaseterms.LeaseTermsPolicyListerViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.numberofids.NumberOfIDsPolicyEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.policies.numberofids.NumberOfIDsPolicyEdtiorView;
import com.propertyvista.crm.client.ui.crud.policies.numberofids.NumberOfIDsPolicyListerView;
import com.propertyvista.crm.client.ui.crud.policies.numberofids.NumberOfIDsPolicyListerViewImpl;

public class PolicyViewFactory extends ViewFactoryBase {
    public static <E extends IEntity, T extends IView<E>> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (NumberOfIDsPolicyListerView.class.equals(type)) {
                map.put(NumberOfIDsPolicyListerView.class, new NumberOfIDsPolicyListerViewImpl());
            } else if (NumberOfIDsPolicyEdtiorView.class.equals(type)) {
                map.put(NumberOfIDsPolicyEdtiorView.class, new NumberOfIDsPolicyEditorViewImpl());

            } else if (LeaseTermsPolicyListerView.class.equals(type)) {
                map.put(LeaseTermsPolicyListerView.class, new LeaseTermsPolicyListerViewImpl());

            } else if (LeaseTermsPolicyEditorView.class.equals(type)) {
                map.put(LeaseTermsPolicyEditorView.class, new LeaseTermsPolicyEditorViewImpl());
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
