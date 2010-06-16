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
 * Created on May 2, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.essentials.client.crud;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MessagePanel extends HorizontalPanel {

    private final HTML messageHolder;

    public MessagePanel() {
        setWidth("100%");
        getElement().getStyle().setMarginBottom(10, Unit.PX);

        messageHolder = new HTML("&nbsp;");
        messageHolder.setWordWrap(false);
        messageHolder.getElement().getStyle().setPadding(6, Unit.PX);
        messageHolder.getElement().getStyle().setBackgroundColor("#FFFBD3");
        messageHolder.getElement().getStyle().setBorderColor("#fbf18f");
        messageHolder.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        messageHolder.getElement().getStyle().setBorderWidth(1, Unit.PX);
        messageHolder.getElement().getStyle().setColor("#000000");
        messageHolder.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        messageHolder.getElement().getStyle().setFontSize(1.1, Unit.EM);
        messageHolder.getElement().getStyle().setProperty("display", "inline-block");
        setMessage(null);

        add(messageHolder);
        setCellHorizontalAlignment(messageHolder, HorizontalPanel.ALIGN_CENTER);
    }

    public void setMessage(String message) {
        messageHolder.setHTML("&nbsp;" + message);
        if (message == null) {
            messageHolder.getElement().getStyle().setVisibility(Visibility.HIDDEN);
        } else {
            messageHolder.getElement().getStyle().setVisibility(Visibility.VISIBLE);
            Timer showTimer = new Timer() {
                @Override
                public void run() {
                    setMessage(null);
                }
            };
            showTimer.scheduleRepeating(30 * 1000);
        }
    }

}
