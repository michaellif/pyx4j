/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2012-12-19
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.client.ui.reports.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

public class ReportTable extends Composite {

    public final class CellData {

        public String value;

        public Map<String, String> styleProperties;

        public CellData(String value, Map<String, String> styleProperties) {
            this.value = value;
            this.styleProperties = styleProperties;
        }
    }

    public interface CellFormatter {

        public CellData formatCell(IEntity rowContext, CellData data, Path path);

    }

    public static abstract class MemberStyleCellFormatter implements CellFormatter {

        private final Map<String, String> styleProperties;

        private final IObject<?> member;

        public MemberStyleCellFormatter(IObject<?> member, Map<String, String> styleProperties) {
            this.styleProperties = styleProperties;
            this.member = member;
        }

        @Override
        public CellData formatCell(IEntity rowContext, CellData data, Path path) {
            if (path.equals(member.getPath())) {
                for (Entry<String, String> property : styleProperties.entrySet()) {
                    data.styleProperties.put(property.getKey(), property.getValue());
                }
                return data;
            } else {
                return data;
            }
        }
    }

    private final HTML reportHtml;

    private final List<MemberColumnDescriptor> columnDescriptors;

    private final List<CellFormatter> formatters;

    public ReportTable(List<MemberColumnDescriptor> columnDescriptors, List<CellFormatter> formatters) {
        this.formatters = formatters;
        this.columnDescriptors = columnDescriptors;
        this.reportHtml = new HTML();

        initWidget(this.reportHtml);
    }

    public <E extends IEntity> void populate(List<E> data) {
        SafeHtmlBuilder bb = new SafeHtmlBuilder();
        bb.appendHtmlConstant("<table style=\"white-space: nowrap; border-collapse: separate; border-spacing: 15pt;\">");
        bb.appendHtmlConstant("<tr>");

        for (ColumnDescriptor desc : columnDescriptors) {
            bb.appendHtmlConstant("<th style=\"text-align: left\">");
            bb.appendEscaped(desc.getColumnTitle());
            bb.appendHtmlConstant("</th>");
        }

        bb.appendHtmlConstant("</tr>");

        for (IEntity entity : data) {
            row(bb, entity);
        }
        bb.appendHtmlConstant("</table>");

        reportHtml.setHTML(bb.toSafeHtml());
    }

    private void row(SafeHtmlBuilder bb, IEntity rowData) {
        bb.appendHtmlConstant("<tr>");
        for (MemberColumnDescriptor columnDescriptor : columnDescriptors) {
            cell(bb, columnDescriptor, rowData);
        }
        bb.appendHtmlConstant("</tr>");
    }

    private void cell(SafeHtmlBuilder bb, MemberColumnDescriptor columnDescriptor, IEntity rowData) {
        CellData cellData = new CellData(columnDescriptor.convert(rowData), new HashMap<String, String>());
        for (CellFormatter formatter : formatters) {
            cellData = formatter.formatCell(rowData, cellData, columnDescriptor.getColumnPath());
        }
        bb.appendHtmlConstant("<td style=\"" + makeStyleAttributeValue(cellData.styleProperties) + "\">");
        bb.appendEscaped(cellData.value);
        bb.appendHtmlConstant("</td>");
    }

    private String makeStyleAttributeValue(Map<String, String> properties) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            stringBuilder.append(property.getKey()).append(":").append(property.getValue()).append("; ");
        }
        return stringBuilder.toString();
    }

}
