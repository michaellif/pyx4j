/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class StaticContentViewImpl extends HorizontalPanel implements StaticContentView {

    private final HTML contentHTML;

    private Presenter presenter;

    public StaticContentViewImpl() {

        contentHTML = new HTML();
        add(contentHTML);

        getElement().getStyle().setMarginLeft(5, Unit.PCT);
        getElement().getStyle().setMarginRight(5, Unit.PCT);
        getElement().getStyle().setMarginTop(15, Unit.PX);
        getElement().getStyle().setMarginBottom(15, Unit.PX);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setContent(String content) {
        contentHTML.setHTML(content);
    }
}
