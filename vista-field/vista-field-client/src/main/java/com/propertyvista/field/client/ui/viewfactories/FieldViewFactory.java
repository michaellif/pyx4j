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
package com.propertyvista.field.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.field.client.ui.FieldScreenView;
import com.propertyvista.field.client.ui.FieldScreenViewImpl;
import com.propertyvista.field.client.ui.RuntimeErrorView;
import com.propertyvista.field.client.ui.RuntimeErrorViewImpl;
import com.propertyvista.field.client.ui.appselection.ApplicationSelectionView;
import com.propertyvista.field.client.ui.appselection.ApplicationSelectionViewImpl;

public class FieldViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (FieldScreenView.class.equals(type)) {
                map.put(type, new FieldScreenViewImpl());
            } else if (RuntimeErrorView.class.equals(type)) {
                map.put(type, new RuntimeErrorViewImpl());
            } else if (ApplicationSelectionView.class.equals(type)) {
                map.put(type, new ApplicationSelectionViewImpl());
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
