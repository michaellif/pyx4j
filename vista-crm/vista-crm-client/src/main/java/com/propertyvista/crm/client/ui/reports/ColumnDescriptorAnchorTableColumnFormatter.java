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
package com.propertyvista.crm.client.ui.reports;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.site.rpc.CrudAppPlace;

public class ColumnDescriptorAnchorTableColumnFormatter implements ITableColumnFormatter {

    protected final ColumnDescriptor columnDescriptor;

    private String styleName;

    private boolean enabled = true;

    private final int width;

    private final Path placeMemberPath;

    public ColumnDescriptorAnchorTableColumnFormatter(int width, ColumnDescriptor columnDescriptor) {
        this.width = width;
        this.columnDescriptor = columnDescriptor;
        this.placeMemberPath = new Path(columnDescriptor.getColumnName().replaceFirst("/$", "_/"));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    @Override
    public SafeHtml formatHeader() {
        return new SafeHtmlBuilder().appendEscaped(columnDescriptor.getColumnTitle()).toSafeHtml();
    }

    @Override
    public SafeHtml formatContent(IEntity entity) {
        SafeHtmlBuilder b = new SafeHtmlBuilder();

        if (!isEnabled() || entity.id().isNull()) {
            if (styleName != null) {
                b.appendHtmlConstant("<div class='" + styleName + "'>");
            }
            b.appendEscaped(columnDescriptor.convert(entity));
            if (styleName != null) {
                b.appendHtmlConstant("</div>");
            }
        } else {
            String url = AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), false, makePlace(entity));
            b.appendHtmlConstant("<a href=\"" + url + "\">");
            if (styleName != null) {
                b.appendHtmlConstant("<div class='" + styleName + "'>");
            }

            b.appendEscaped(columnDescriptor.convert(entity));
            if (styleName != null) {
                b.appendHtmlConstant("</div>");
            }

            b.appendHtmlConstant("</a>");
        }

        return b.toSafeHtml();
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    protected CrudAppPlace makePlace(IEntity entity) {
        IEntity placeMember = (IEntity) entity.getMember(placeMemberPath);
        return AppPlaceEntityMapper.resolvePlace(placeMember.getInstanceValueClass(), placeMember.getPrimaryKey());
    }
}
