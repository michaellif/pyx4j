/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-21
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.decorations;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;

import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;

public class CrmSectionSeparator extends VistaHeaderBar {

    public static String DEFAULT_STYLE_PREFIX = "vista_CrmSectionSeparator";

    public CrmSectionSeparator(String caption, Widget widget, String width) {
        super(caption, widget, width);
        setStyleName(DEFAULT_STYLE_PREFIX);
    }

    public CrmSectionSeparator(String caption, Widget widget) {
        this(caption, widget, null);
    }

    public CrmSectionSeparator(String caption, String width) {
        this(caption, null, width);
    }

    public CrmSectionSeparator(String caption) {
        this(caption, (String) null);
    }

    public CrmSectionSeparator(IObject<?> member, String width) {
        this(member.getMeta().getCaption(), null, width);
    }

    public CrmSectionSeparator(IObject<?> member) {
        this(member, null);
    }

    @Override
    protected String getStylePrefix() {
        return this.DEFAULT_STYLE_PREFIX;
    }
}