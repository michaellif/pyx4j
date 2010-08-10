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

import com.pyx4j.widgets.client.dialog.Dialog;

public class RichTextAreaImplIE6Fixed extends RichTextAreaImplIE6 {

    private static final Logger log = LoggerFactory.getLogger(RichTextAreaImplIE6Fixed.class);

    @Override
    public native void initElement() /*-{
        var _this = this;
        _this.@com.google.gwt.user.client.ui.impl.RichTextAreaImplStandard::initializing = true;

        setTimeout(function() {
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

        var disabled = doc.body.disabled;

        if(disabled == false)
        {
        //make sure the body is not given editing focus automatically
        //when contentEditable is set to true, which prevents it from connecting to its event handlers properly
        doc.body.disabled = true;
        }

        doc.body.contentEditable = true;

        doc.body.disabled = disabled;

        // Send notification that the iframe has reached design mode.
        _this.@com.google.gwt.user.client.ui.impl.RichTextAreaImplStandard::onElementInitialized()();             
        }, 1);
    }-*/;

    @Override
    protected void hookEvents() {
        super.hookEvents();
        hookEventsExtra();
        setFocus(true);
        setFocus(false);
    }

    private native void hookEventsExtra() /*-{
        var elem = this.@com.google.gwt.user.client.ui.impl.RichTextAreaImpl::elem;
        var body = elem.contentWindow.document.body;

        //Make sure the cursor gets removed from the editor any time it loses focus - watching this body event does the trick in all cases I've tried
        body.onblur = function()
        {
        if(body.contentEditable)
        {
        //These steps must be executed in this order - any variation seems to either make the body remain editable (even if disabled, strangely),
        //or prevent it from becoming editable again. It's important that contentEditable be restored before the next onFocus call so that the
        //editor can receive drag/dropped text
        body.contentEditable = false;
        body.disabled = true;
        body.contentEditable = true;
        body.disabled = false;
        }
        }
    }-*/;

    @Override
    protected void unhookEvents() {
        super.unhookEvents();
        unhookEventsExtra();
    }

    private native void unhookEventsExtra() /*-{
        var elem = this.@com.google.gwt.user.client.ui.impl.RichTextAreaImpl::elem;
        var body = elem.contentWindow.document.body;

        if (body)
        {
        //Remove the blur watcher
        body.onblur = null;
        }
    }-*/;
}