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
package com.propertyvista.portal.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class MainNavigViewImpl extends SimplePanel implements MainNavigView {

    public static String DEFAULT_STYLE_PREFIX = "vista_Steps";

    public static enum StyleSuffix implements IStyleSuffix {
        Holder, Tab, LabelHolder, StatusHolder, Label
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, latest, complete, invalid, current
    }

    private MainNavigPresenter presenter;

    private NavigTabList tabsHolder;

    public MainNavigViewImpl() {
        setStyleName(DEFAULT_STYLE_PREFIX);
    }

    @Override
    public void setPresenter(MainNavigPresenter presenter) {
        this.presenter = presenter;

        clear();
        tabsHolder = new NavigTabList();
        setWidget(tabsHolder);

    }

    class NavigTabList extends ComplexPanel {
        public NavigTabList() {
            setElement(DOM.createElement("ul"));
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Holder.name());
        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }
    }
}
