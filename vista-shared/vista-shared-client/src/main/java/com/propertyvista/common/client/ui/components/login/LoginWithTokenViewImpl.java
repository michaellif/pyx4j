/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.login;

import static com.pyx4j.commons.HtmlUtils.h2;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;

public class LoginWithTokenViewImpl implements LoginWithTokenView {

    private static final I18n i18n = I18n.get(LoginWithTokenViewImpl.class);

    @Override
    public Widget asWidget() {
        VerticalPanel main = new VerticalPanel();
        main.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        main.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        main.setWidth("100%");

        HTML header = new HTML(h2(i18n.tr("Authenticating...")));
        header.getElement().getStyle().setMargin(3, Unit.EM);
        header.getElement().getStyle().setProperty("textAlign", "center");
        main.add(header);
        return main;
    }

}
