/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.svg.gadgets.util;

import java.util.Iterator;

public class LabelHelper {

    public static String makeListView(Iterable<?> col) {
        StringBuilder viewBuilder = new StringBuilder();
        Iterator<?> i = col.iterator();
        if (i.hasNext()) {
            viewBuilder.append(i.next().toString());
        }
        while (i.hasNext()) {
            viewBuilder.append(", ").append(i.next().toString());
        }
        return viewBuilder.toString();

    }

}
