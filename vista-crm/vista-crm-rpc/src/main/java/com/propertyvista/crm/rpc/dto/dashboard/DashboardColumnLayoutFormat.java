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
 */
package com.propertyvista.crm.rpc.dto.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;

/**
 * Represents layout of the gadgets in a dashboard.
 * Should be use to as a convinence to access layout information which is stored as plain text in {@link DashboardMetadata#encodedLayout()}
 */
//TODO think about better name for this class
public class DashboardColumnLayoutFormat {

    public static class Builder {

        private final DashboardColumnLayoutFormat format;

        public Builder(DashboardMetadata.LayoutType layoutType) {
            this.format = new DashboardColumnLayoutFormat(layoutType.name().toString() + "\n");
        }

        public Builder bind(String gadgetId, int column) {
            format.gadgetBinding.put(gadgetId, column);
            format.gadgetOrder.add(gadgetId);
            return this;
        }

        public DashboardColumnLayoutFormat build() {
            return format;
        }

    }

    private final DashboardMetadata.LayoutType layoutType;

    private final Map<String, Integer> gadgetBinding;

    private final List<String> gadgetOrder;

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
        gadgetOrder = new ArrayList<String>();
        for (int i = 1; i < temp.length; ++i) {
            String[] rawBinding = temp[i].split(" ");
            Integer column = Integer.parseInt(rawBinding[1]);
            String gadgetId = rawBinding[0];
            gadgetBinding.put(gadgetId, column);
            gadgetOrder.add(gadgetId);
        }
    }

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public String getSerializedForm() {
        StringBuilder builder = new StringBuilder();
        builder.append(getLayoutType().name().toString()).append("\n");
        for (String gadgetId : gadgetOrder) {
            String column = "" + getGadgetColumn(gadgetId);
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

    public List<String> gadgetIds() {
        return gadgetOrder;
    }

}
