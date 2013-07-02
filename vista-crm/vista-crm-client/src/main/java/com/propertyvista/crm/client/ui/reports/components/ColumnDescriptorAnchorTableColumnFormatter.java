/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.site.rpc.CrudAppPlace;

public abstract class ColumnDescriptorAnchorTableColumnFormatter implements ITableColumnFormatter {

    protected final ColumnDescriptor columnDescriptor;

    private final boolean linkOptional;

    private final int width;

    private final String styleName;

    public ColumnDescriptorAnchorTableColumnFormatter(int width, ColumnDescriptor columnDescriptor) {
        this(width, null, columnDescriptor, false);
    }

    public ColumnDescriptorAnchorTableColumnFormatter(int width, String styleName, ColumnDescriptor columnDescriptor, boolean linkOptional) {
        this.width = width;
        this.columnDescriptor = columnDescriptor;
        this.linkOptional = linkOptional;
        this.styleName = styleName;
    }

    @Override
    public SafeHtml formatHeader() {
        return new SafeHtmlBuilder().appendEscaped(columnDescriptor.getColumnTitle()).toSafeHtml();
    }

    @Override
    public SafeHtml formatContent(IEntity entity) {
        if (linkOptional && entity.id().isNull()) {
            SafeHtmlBuilder b = new SafeHtmlBuilder();
            if (styleName != null) {
                b.appendHtmlConstant("<div class='" + styleName + "'>");
            }
            b.appendEscaped(columnDescriptor.convert(entity));
            if (styleName != null) {
                b.appendHtmlConstant("</div>");
            }

            return b.toSafeHtml();
        } else {

            String url = AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), false, makePlace(entity));
            SafeHtmlBuilder b = new SafeHtmlBuilder();
            b.appendHtmlConstant("<a href=\"" + url + "\">");
            if (styleName != null) {
                b.appendHtmlConstant("<div class='" + styleName + "'>");
            }

            b.appendEscaped(columnDescriptor.convert(entity));
            if (styleName != null) {
                b.appendHtmlConstant("</div>");
            }

            b.appendHtmlConstant("</a>");
            return b.toSafeHtml();
        }

    }

    @Override
    public int getWidth() {
        return this.width;
    }

    protected abstract CrudAppPlace makePlace(IEntity entity);
}
