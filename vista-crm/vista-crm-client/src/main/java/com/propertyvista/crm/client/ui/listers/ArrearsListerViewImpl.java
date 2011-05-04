/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.listers;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ArrearsListerViewImpl extends SimplePanel implements ArrearsListerView {

    private static I18n i18n = I18nFactory.getI18n(ArrearsListerViewImpl.class);

    public ArrearsListerViewImpl() {
        VerticalPanel main = new VerticalPanel();

        // TODO - fill main here
        main.add(new HTML("Lister content goes here..."));

        main.setSize("100%", "100%");
        setWidget(main);
    }
}
