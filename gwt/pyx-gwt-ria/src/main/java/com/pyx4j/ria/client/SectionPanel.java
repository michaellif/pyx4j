/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Apr 21, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SectionPanel extends LayoutPanel {

    private final DockLayoutPanel contentPanel;

    private final DockLayoutPanel rootPanel;

    private final ScrollPanel scrollPanel;

    private final SimplePanel header1Mark;

    private final SimplePanel header2Mark;

    private final SimplePanel footerMark;

    public SectionPanel() {

        super();

        rootPanel = new DockLayoutPanel(Unit.EM);
        rootPanel.getElement().getStyle().setBorderColor("black");
        rootPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        rootPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);

        header1Mark = new SimplePanel();
        rootPanel.addNorth(header1Mark, 0);

        contentPanel = new DockLayoutPanel(Unit.EM);
        contentPanel.getElement().getStyle().setBorderColor("#86adc4");
        contentPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        contentPanel.getElement().getStyle().setBorderWidth(2, Unit.PX);

        header2Mark = new SimplePanel();
        contentPanel.addNorth(header2Mark, 0);

        footerMark = new SimplePanel();
        contentPanel.addSouth(footerMark, 0);

        scrollPanel = new ScrollPanel();
        scrollPanel.getElement().getStyle().setBackgroundColor("white");

        contentPanel.add(scrollPanel);

        rootPanel.add(contentPanel);

        add(rootPanel);

    }

    protected void setHeader1Pane(Widget headerPane) {
        rootPanel.insertNorth(headerPane, 1.6, header1Mark);
    }

    protected void setHeader2Pane(Widget headerPane) {
        contentPanel.insertNorth(headerPane, 2, header2Mark);
    }

    protected void setFooterPane(Widget footerPane) {
        contentPanel.insertSouth(footerPane, 1.5, footerMark);
    }

    protected void setContentPane(Widget contentPane) {
        scrollPanel.setWidget(contentPane);
    }

    public int getVerticalScrollPosition() {
        return scrollPanel.getScrollPosition();
    }

    public void setVerticalScrollPosition(int position) {
        scrollPanel.setScrollPosition(position);
    }

    public void scrollToBottom() {
        scrollPanel.scrollToBottom();
    }

    public int getHorizontalScrollPosition() {
        return scrollPanel.getHorizontalScrollPosition();

    }

    public void setHorizontalScrollPosition(int position) {
        scrollPanel.setHorizontalScrollPosition(position);
    }

}
