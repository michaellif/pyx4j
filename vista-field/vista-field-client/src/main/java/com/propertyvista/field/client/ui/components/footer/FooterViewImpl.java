/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.field.client.ui.components.footer;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.field.client.theme.FieldTheme;

public class FooterViewImpl extends SimplePanel implements FooterView {

    public FooterViewImpl() {

        setStyleName(FieldTheme.StyleName.PageFooter.name());

        getElement().getStyle().setBackgroundColor("#bca");
        setHeight("50px");

        setWidget(new Label("Footer"));

    }

}
