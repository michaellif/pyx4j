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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.widgets.client.richtext.ExtendedRichTextArea;
import com.pyx4j.widgets.client.richtext.RichTextImageProvider;

public class NRichTextArea extends NTextComponent<String, ExtendedRichTextArea, CRichTextArea> {
    public NRichTextArea(CRichTextArea textArea) {
        super(textArea);
        textArea.asWidget().setWidth("100%");

        getElement().getStyle().setProperty("resize", "none");
        sinkEvents(Event.ONMOUSEOVER);
        sinkEvents(Event.ONMOUSEOUT);
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().setImageProvider(getCComponent().getImageProvider());
    }

    @Override
    protected ExtendedRichTextArea createEditor() {
        ExtendedRichTextArea area = new ExtendedRichTextArea();
        area.setHeight("20em");
        return area;
    }

    @Override
    protected HTML createViewer() {
        HTML viewer = super.createViewer();
        //TODO move to styles and make it tidy
        viewer.getElement().getStyle().setProperty("overflow", "auto");
        viewer.getElement().getStyle().setProperty("height", "100%");
        viewer.getElement().getStyle().setProperty("maxHeight", "15em");
        viewer.getElement().getStyle().setProperty("background", "#fafafa");
        viewer.getElement().getStyle().setProperty("padding", "2px");
        return viewer;
    }

    public void scrollToBottom() {
        if (getEditor() != null) {
            getEditor().scrollToBottom();
        }
    }

    @Override
    public void setNativeValue(String value) {
        if (isViewable()) {
            getViewer().setHTML(value);
        } else {
            getEditor().setText(value);
        }
    }

    @Override
    public String getNativeValue() {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            return getEditor().getText();
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (getEditor() != null) {
            getEditor().onBrowserEvent(event);
        }
    }

    public void setImageProvider(RichTextImageProvider provider) {
        if (getEditor() != null) {
            getEditor().setImageProvider(provider);
        }
    }

}