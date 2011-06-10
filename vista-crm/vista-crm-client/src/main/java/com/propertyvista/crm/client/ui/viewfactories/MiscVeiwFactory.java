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
package com.propertyvista.crm.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.crm.client.ui.AccountView;
import com.propertyvista.crm.client.ui.AccountViewImpl;
import com.propertyvista.crm.client.ui.AlertView;
import com.propertyvista.crm.client.ui.AlertViewImpl;

public class MiscVeiwFactory extends ViewFactoryBase {

    public static IsWidget instance(Class<?> type) {
        if (!map.containsKey(type)) {
            if (AccountView.class.equals(type)) {
                map.put(type, new AccountViewImpl());
            } else if (AlertView.class.equals(type)) {
                map.put(type, new AlertViewImpl());
            }
        }
        return map.get(type);
    }
}
