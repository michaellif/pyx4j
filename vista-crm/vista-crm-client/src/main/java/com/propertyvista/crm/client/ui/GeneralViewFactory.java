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
package com.propertyvista.crm.client.ui;

import java.util.HashMap;

import com.google.gwt.user.client.ui.IsWidget;

public class GeneralViewFactory {

    private static HashMap<Class<? extends IsWidget>, IsWidget> map = new HashMap<Class<? extends IsWidget>, IsWidget>();

    @SuppressWarnings("unchecked")
    public static <E extends IsWidget> E instance(Class<E> type) {
        if (!map.containsKey(type)) {
            if (MessageView.class.equals(type)) {
                map.put(type, new MessageViewImpl());
            } else if (MessageView.class.equals(type)) {
                map.put(type, new MessageViewImpl());
            }
        }
        return (E) map.get(type);
    }
}
