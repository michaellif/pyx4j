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
package com.propertyvista.admin.client.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.admin.client.ui.crud.pmc.PmcEditorView;
import com.propertyvista.admin.client.ui.crud.pmc.PmcEditorViewImpl;
import com.propertyvista.admin.client.ui.crud.pmc.PmcListerView;
import com.propertyvista.admin.client.ui.crud.pmc.PmcListerViewImpl;
import com.propertyvista.admin.client.ui.crud.pmc.PmcViewerView;
import com.propertyvista.admin.client.ui.crud.pmc.PmcViewerViewImpl;
import com.propertyvista.common.client.viewfactories.ViewFactoryBase;

public class ManagementVeiwFactory extends ViewFactoryBase {

    public static IsWidget instance(Class<?> type) {
        if (!map.containsKey(type)) {
            if (PmcListerView.class.equals(type)) {
                map.put(type, new PmcListerViewImpl());
            } else if (PmcViewerView.class.equals(type)) {
                map.put(type, new PmcViewerViewImpl());
            } else if (PmcEditorView.class.equals(type)) {
                map.put(type, new PmcEditorViewImpl());
            }
        }
        return map.get(type);
    }
}
