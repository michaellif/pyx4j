/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import java.util.HashMap;

import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.crm.client.ui.crud.IView;
import com.propertyvista.crm.client.ui.crud.settings.ContentEditor;
import com.propertyvista.crm.client.ui.crud.settings.ContentEditorImpl;

public class SettingsViewFactory {

    private static HashMap<Class<? extends IView<?>>, IView<?>> map = new HashMap<Class<? extends IView<?>>, IView<?>>();

    public static IView<? extends IEntity> instance(Class<? extends IView<? extends IEntity>> type) {
        if (!map.containsKey(type)) {
            if (ContentEditor.class.equals(type)) {
                map.put(type, new ContentEditorImpl());
            }
        }
        return map.get(type);
    }
}
