/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.autopay;

class ColumnGroup {

    public final String groupTitle;

    public final ColumnGroup parentGroup;

    public final int level;

    public int childCount = 0;

    public ColumnGroup(String groupTitle, ColumnGroup parentGroup) {
        this.groupTitle = groupTitle;
        this.parentGroup = parentGroup;

        if (parentGroup != null) {
            parentGroup.childCount += 1;
        }

        ColumnGroup currentGroup = parentGroup;
        int level = 0;
        while (currentGroup != null) {
            currentGroup = currentGroup.parentGroup;
            level += 1;
        }
        this.level = level;
    }
}