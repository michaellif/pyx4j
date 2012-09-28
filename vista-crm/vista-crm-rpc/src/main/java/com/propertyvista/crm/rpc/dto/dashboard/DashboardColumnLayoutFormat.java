/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 28, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.dashboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;

public class DashboardColumnLayoutFormat {

    public static class Builder {

        private final DashboardColumnLayoutFormat format;

        public Builder(DashboardMetadata.LayoutType layoutType) {
            this.format = new DashboardColumnLayoutFormat(layoutType.name().toString() + "\n");
        }

        public Builder bind(String gadgetId, int column) {
            format.gadgetBinding.put(gadgetId, column);
            return this;
        }

        public DashboardColumnLayoutFormat build() {
            return format;
        }

    }

    private final DashboardMetadata.LayoutType layoutType;

    private final Map<String, Integer> gadgetBinding;

    /**
     * @param serializedFormat
     *            layout stored as:
     * 
     *            <pre>
     * layoutType:DashboardMetadata.Layout.Type\n
     * (gadgetId:String column:Integer\n)*
     * </pre>
     */
    public DashboardColumnLayoutFormat(String serializedFormat) {
        String[] temp = serializedFormat.split("\n");
        layoutType = LayoutType.valueOf(temp[0]);
        gadgetBinding = new HashMap<String, Integer>();
        for (int i = 1; i < temp.length; ++i) {
            String[] rawBinding = temp[i].split(" ");
            Integer column = Integer.parseInt(rawBinding[1]);
            String gadgetId = rawBinding[0];
            gadgetBinding.put(gadgetId, column);
        }
    }

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public String getSerializedForm() {
        StringBuilder builder = new StringBuilder();
        builder.append(getLayoutType().name().toString()).append("\n");
        for (Entry<String, Integer> entry : gadgetBinding.entrySet()) {
            String gadgetId = entry.getKey().toString();
            String column = entry.getValue().toString();
            builder.append(gadgetId).append(' ').append(column).append("\n");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return getSerializedForm();
    }

    public int getGadgetColumn(String gadgetId) {
        return gadgetBinding.get(gadgetId);
    }

    public Set<String> gadgetIds() {
        return gadgetBinding.keySet();
    }

}
