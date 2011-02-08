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
 * Created on Apr 24, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.essentials.client.crud;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;

public class ActionsBoxPanel extends ActionsPanel {

    private final VerticalPanel contentPanel;

    public ActionsBoxPanel() {
        setWidth("100%");

        FieldSetPanel fieldSetPanel = new FieldSetPanel();

        SimplePanel fieldSetHolderPanel = new SimplePanel();

        fieldSetHolderPanel.getElement().getStyle().setPadding(2, Unit.PX);
        fieldSetHolderPanel.getElement().getStyle().setMarginBottom(10, Unit.PX);
        fieldSetHolderPanel.getElement().getStyle().setBackgroundColor("#FFFBD3");
        fieldSetHolderPanel.getElement().getStyle().setBorderColor("#fbf18f");
        fieldSetHolderPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        fieldSetHolderPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);

        fieldSetHolderPanel.setWidget(fieldSetPanel);
        add(fieldSetHolderPanel);

        LegendPanel legend = new LegendPanel();
        fieldSetPanel.add(legend);

        contentPanel = new VerticalPanel();
        fieldSetPanel.add(contentPanel);
    }

    @Deprecated
    // use IDebugId
    public Anchor addItem(String name, ClickHandler handler) {
        return addItem(name, null, handler);
    }

    public Anchor addItem(String name, IDebugId debugId, ClickHandler handler) {
        Anchor anchor = new Anchor(name);
        anchor.getElement().getStyle().setColor("#0066CC");
        anchor.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        anchor.getElement().getStyle().setFontSize(1.15, Unit.EM);
        anchor.getElement().getStyle().setMargin(10, Unit.PX);
        anchor.getElement().getStyle().setProperty("lineHeight", "1.7em");

        if (handler != null) {
            anchor.addClickHandler(handler);
        }
        if (debugId != null) {
            anchor.ensureDebugId(debugId.getDebugIdString());
        } else {
            anchor.ensureDebugId(name);
        }

        contentPanel.add(anchor);
        return anchor;

    }

    class LegendPanel extends ComplexPanel {

        public LegendPanel() {
            super();
            setElement(Document.get().createLegendElement());

            InlineHTML caption = new InlineHTML("Actions");

            Style captionStyle = caption.getElement().getStyle();
            captionStyle.setProperty("padding", "5px 2px 2px 2px");
            captionStyle.setProperty("verticalAlign", "top");
            captionStyle.setColor("#E58F00");

            add(caption, getElement());

        }

    }

    class FieldSetPanel extends ComplexPanel {

        public FieldSetPanel() {
            super();
            setElement(Document.get().createFieldSetElement());
            getElement().getStyle().setBorderColor("#E58F00");
            getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
            getElement().getStyle().setBorderWidth(1, Unit.PX);
            getElement().getStyle().setProperty("color", "#387cbb");
            getElement().getStyle().setMargin(3, Unit.PX);
            getElement().getStyle().setPadding(5, Unit.PX);
        }

        @Override
        public void add(Widget w) {
            add(w, getElement());
        }

    }

}
