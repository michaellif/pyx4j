/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.viewfactories.ViewFactoryBase;
import com.propertyvista.crm.client.ui.reports.AutoPayReviewUpdaterView;
import com.propertyvista.crm.client.ui.reports.AutoPayReviewUpdaterViewImpl;
import com.propertyvista.crm.client.ui.reports.CrmReportsView;
import com.propertyvista.crm.client.ui.reports.CrmReportsViewImpl;

public class ReportsViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (CrmReportsView.class.equals(type)) {
                map.put(type, new CrmReportsViewImpl());
            } else if (AutoPayReviewUpdaterView.class.equals(type)) {
                map.put(type, new AutoPayReviewUpdaterViewImpl());
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
