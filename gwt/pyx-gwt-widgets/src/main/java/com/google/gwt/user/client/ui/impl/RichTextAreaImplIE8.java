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
 * Created on Aug 10, 2010
 * @author michaellif
 * @version $Id$
 */
package com.google.gwt.user.client.ui.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.widgets.client.dialog.Dialog;

public class RichTextAreaImplIE8 extends RichTextAreaImplStandard {

    private static final Logger log = LoggerFactory.getLogger(RichTextAreaImplIE8.class);

    @Override
    public Element createElement() {
        Element elem = super.createElement();
        DOM.setElementProperty(elem, "src", "javascript:''");
        return elem;
    }

    @Override
    public void initElement() {
        initElementNative();
        log.info("++++++++ After initElement");
    }

    @Override
    public void uninitElement() {
        super.uninitElement();
        RootPanel.get().getElement().focus();
        log.info("++++++++ After uninitElement");
    }

    public native void initElementNative() /*-{
        var _this = this;
        _this.@com.google.gwt.user.client.ui.impl.RichTextAreaImplStandard::initializing = true;

        setTimeout($entry(function() {
        if (_this.@com.google.gwt.user.client.ui.impl.RichTextAreaImplStandard::initializing == false) {
        return;
        }

        // Attempt to set the iframe document's body to 'contentEditable' mode.
        // There's no way to know when the body will actually be available, so
        // keep trying every so often until it is.
        // Note: The body seems to be missing only rarely, so please don't remove
        // this retry loop just because it's hard to reproduce.
        var elem = _this.@com.google.gwt.user.client.ui.impl.RichTextAreaImpl::elem;
        var doc = elem.contentWindow.document;
        if (!doc.body) {
        // Retry in 50 ms. Faster would run the risk of pegging the CPU. Slower
        // would increase the probability of a user-visible delay.
        setTimeout(arguments.callee, 50);
        return;
        }
        doc.body.contentEditable = true;

        // Send notification that the iframe has reached design mode.
        _this.@com.google.gwt.user.client.ui.impl.RichTextAreaImplStandard::onElementInitialized()();
        }, 1));
    }-*/;

    @Override
    public native void insertHTML(String html) /*-{
        try {
        var elem = this.@com.google.gwt.user.client.ui.impl.RichTextAreaImpl::elem;
        var doc = elem.contentWindow.document;
        elem.contentWindow.focus();
        var tr = doc.selection.createRange();
        if (tr == null) {
        return;
        }
        if (!@com.google.gwt.user.client.DOM::isOrHasChild(Lcom/google/gwt/user/client/Element;Lcom/google/gwt/user/client/Element;)(doc.body, tr.parentElement())) {
        return;
        }
        tr.pasteHTML(html);
        }
        catch (e) {
        return;
        }
    }-*/;

    @Override
    protected native String getTextImpl() /*-{
        var elem = this.@com.google.gwt.user.client.ui.impl.RichTextAreaImpl::elem;
        return elem.contentWindow.document.body.innerText;
    }-*/;

    @Override
    protected void hookEvents() {
        hookEventsNative();
        log.info("++++++++ After hookEvents");
    }

    protected native void hookEventsNative() /*-{
        var elem = this.@com.google.gwt.user.client.ui.impl.RichTextAreaImpl::elem;
        var body = elem.contentWindow.document.body;

        var handler = $entry(function() {
        if (elem.__listener) {
        // Weird: this code has the context of the script frame, but we need the
        // event from the edit iframe's window.
        var evt = elem.contentWindow.event;
        elem.__listener.@com.google.gwt.user.client.ui.Widget::onBrowserEvent(Lcom/google/gwt/user/client/Event;)(evt);
        }
        });

        body.onkeydown =
        body.onkeyup =
        body.onkeypress =
        body.onmousedown =
        body.onmouseup =
        body.onmousemove =
        body.onmouseover =
        body.onmouseout =
        body.onclick = handler;

        elem.contentWindow.onfocus =
        elem.contentWindow.onblur = handler;
    }-*/;

    @Override
    protected native void setTextImpl(String text) /*-{
        var elem = this.@com.google.gwt.user.client.ui.impl.RichTextAreaImpl::elem;
        elem.contentWindow.document.body.innerText = text;
    }-*/;

    @Override
    protected void unhookEvents() {
        unhookEventsNative();
        log.info("++++++++ After unhookEvents");
    }

    protected native void unhookEventsNative() /*-{
        var elem = this.@com.google.gwt.user.client.ui.impl.RichTextAreaImpl::elem;
        var body = elem.contentWindow.document.body;

        if (body) {
        // The body can be undefined in the relatively obscure case that the RTA
        // is attached and detached before it has a chance to finish initializing.
        body.onkeydown =
        body.onkeyup =
        body.onkeypress =
        body.onmousedown =
        body.onmouseup =
        body.onmousemove =
        body.onmouseover =
        body.onmouseout =
        body.onclick = null;

        elem.contentWindow.onfocus =
        elem.contentWindow.onblur = null;
        }
    }-*/;
}