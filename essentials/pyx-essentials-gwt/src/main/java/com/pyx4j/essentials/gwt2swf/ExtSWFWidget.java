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
 * Created on 2010-04-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.gwt2swf;

import java.util.Map;

import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;

import com.pyx4j.gwt.commons.AjaxJSLoader;

public class ExtSWFWidget extends SWFWidget {

    public ExtSWFWidget(String src, int width, int height) {
        super(src, width, height);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void injectSWF(final String swf, final String id, final String w, final String h, final String ver, final Map flashvars, final Map params,
            final Map attributes) {

        AjaxJSLoader.load("ajax.googleapis.com/ajax/libs/swfobject/2/swfobject.js", new AjaxJSLoader.IsJSLoaded() {

            @Override
            public native boolean isLoaded()
            /*-{ return typeof $wnd.swfobject != "undefined"; }-*/;

        }, new Runnable() {

            @Override
            public void run() {
                ExtSWFWidget.super.injectSWF(swf, id, w, h, ver, flashvars, params, attributes);
            }

        });
    }

    @Override
    public String getSwfId() {
        return super.getSwfId();
    }

    protected void allowScriptAccess() {
        this.addParam("allowScriptAccess", "always");
    }

    public void allowFullscreen() {
        this.addParam("allowfullscreen", "true");
    }

}
