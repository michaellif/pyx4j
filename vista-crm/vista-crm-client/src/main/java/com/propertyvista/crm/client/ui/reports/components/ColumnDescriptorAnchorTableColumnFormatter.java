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

    public ColumnDescriptorAnchorTableColumnFormatter(int width, ColumnDescriptor columnDescriptor) {
        this(width, columnDescriptor, false);
    }

    public ColumnDescriptorAnchorTableColumnFormatter(int width, ColumnDescriptor columnDescriptor, boolean linkOptional) {
        this.width = width;
        this.columnDescriptor = columnDescriptor;
        this.linkOptional = linkOptional;
    }

    @Override
    public SafeHtml formatHeader() {
        return new SafeHtmlBuilder().appendEscaped(columnDescriptor.getColumnTitle()).toSafeHtml();
    }

    @Override
    public SafeHtml formatContent(IEntity entity) {
        if (linkOptional && entity.id().isNull()) {
            return new SafeHtmlBuilder().appendEscaped(columnDescriptor.convert(entity)).toSafeHtml();
        } else {
            //@formatter:off
            String url = AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), false, makePlace(entity));
            return new SafeHtmlBuilder()
                .appendHtmlConstant("<a href=\"" + url + "\">")
                .appendEscaped(columnDescriptor.convert(entity))
                .appendHtmlConstant("</a>")
                .toSafeHtml();
            //@formatter:on
        }

    }

    @Override
    public int getWidth() {
        return this.width;
    }

    protected abstract CrudAppPlace makePlace(IEntity entity);
}
