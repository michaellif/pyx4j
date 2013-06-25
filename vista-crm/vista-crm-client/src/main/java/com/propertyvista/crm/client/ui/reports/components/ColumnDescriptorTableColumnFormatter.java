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

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;

public class ColumnDescriptorTableColumnFormatter implements ITableColumnFormatter {

    private final ColumnDescriptor columnDescriptor;

    private final int width;

    public ColumnDescriptorTableColumnFormatter(int width, ColumnDescriptor columnDescriptor) {
        this.columnDescriptor = columnDescriptor;
        this.width = width;
    }

    @Override
    public SafeHtml formatHeader() {
        return new SafeHtmlBuilder().appendEscaped(columnDescriptor.getColumnTitle()).toSafeHtml();
    }

    @Override
    public SafeHtml formatContent(IEntity entity) {
        return new SafeHtmlBuilder().appendEscaped(columnDescriptor.convert(entity)).toSafeHtml();
    }

    @Override
    public int getWidth() {
        return this.width;
    }

}
