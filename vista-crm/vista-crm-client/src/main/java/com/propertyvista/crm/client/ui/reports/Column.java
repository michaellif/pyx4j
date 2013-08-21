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
package com.propertyvista.crm.client.ui.reports;

import com.google.gwt.user.client.Window;

public class Column {

    public final String name;

    public final ColumnGroup parentGroup;

    public int level;

    private int effectiveWidth = -1;

    public Column(String name, ColumnGroup parentGroup, int[] widthsPerScreenResolution) {
        this.name = name;
        this.parentGroup = parentGroup;
        this.effectiveWidth = widthsPerScreenResolution[widthsPerScreenResolution.length - 1];
        int clientWidth = Window.getClientWidth();
        for (int i = 0; i < widthsPerScreenResolution.length - 1; i += 2) {
            if (widthsPerScreenResolution[i] <= clientWidth) {
                this.effectiveWidth = widthsPerScreenResolution[i + 1];
                break;
            }
        }

        this.level = parentGroup == null ? 0 : parentGroup.level + 1;
        if (parentGroup != null) {
            parentGroup.childCount += 1;
        }
    }

    public int getEffectiveWidth() {
        return this.effectiveWidth;

    }
}