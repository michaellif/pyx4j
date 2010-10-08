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
 * Created on 2010-10-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.dnd.client;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.widgets.client.util.BrowserType;

public class DataTransfer extends JavaScriptObject {

    public static final String TYPE_TEXT = "text/plain";

    public static final String TYPE_HTML = "text/html";

    public static final String TYPE_URL = "text/uri-list";

    protected DataTransfer() {
    }

    public final DropEffect getDropEffect() {
        return Enum.valueOf(DropEffect.class, getDropEffectN());
    }

    private native String getDropEffectN() /*-{
        return this.dropEffect;
    }-*/;

    public final void setDropEffect(DropEffect dropEffect) {
        setDropEffectN(dropEffect.name());
    }

    private native void setDropEffectN(String dropEffect) /*-{
        this.dropEffect = dropEffect;
    }-*/;

    public final DragEffect getEffectAllowed() {
        String ea;
        try {
            ea = getEffectAllowedN();
        } catch (Throwable ie) {
            return null;
        }
        if ("uninitialized".equals(ea)) {
            return null;
        } else {
            return Enum.valueOf(DragEffect.class, ea);
        }
    }

    private native String getEffectAllowedN() /*-{
        return this.effectAllowed;
    }-*/;

    public final void setEffectAllowed(DragEffect effectAllowed) {
        setEffectAllowedN(effectAllowed.name());
    }

    private native void setEffectAllowedN(String effectAllowed) /*-{
        this.effectAllowed = effectAllowed;
    }-*/;

    /**
     * Warning: This method is not supported in IE!
     */
    public final native String[] getTypes() /*-{
        return this.types;
    }-*/;

    public final native String getData(String format) /*-{
        return this.getData(format);
    }-*/;

    public final native void setData(String format, String data) /*-{
        this.setData(format, data);
    }-*/;

    public final native void clearData(String format) /*-{
        this.clearData(format);
    }-*/;

    public final void setDragImage(Image image, int x, int y) {
        setDragImageN(image.getElement(), x, y);
    }

    public final native void setDragImageN(Element elt, int x, int y) /*-{
        this.setDragImage(elt, x, y);
    }-*/;

    public final String toDebugString() {
        StringBuilder b = new StringBuilder();
        try {
            String[] typesArray = getTypes();
            if (typesArray != null) {
                try {
                    List<String> types = Arrays.asList(typesArray);
                    b.append("\n types: ").append(ConverterUtils.convertStringCollection(types));
                    if (types.contains(TYPE_URL)) {
                        try {
                            b.append("\n URL : ").append(getData(TYPE_URL));
                        } catch (Throwable e) {
                            b.append("n/a");
                        }
                    }

                    if (types.contains(TYPE_TEXT)) {
                        try {
                            b.append("\n TEXT: ").append(getData(TYPE_TEXT));
                        } catch (Throwable e) {
                            b.append("n/a");
                        }
                    }
                } catch (Throwable e) {
                    b.append("get types error").append(e);
                }
            } else if (BrowserType.isIE()) {
                String t = getData("Text");
                if (t != null) {
                    b.append("\n Text: ").append(t);
                }
                t = getData("URL");
                if (t != null) {
                    b.append("\n URL : ").append(t);
                }
            }
            b.append("\n effectAllowed : ").append(getEffectAllowed());
            b.append("\n dropEffect    : ").append(getDropEffect());
        } catch (Throwable e) {
            b.append("; error : ").append(e);
        }
        return b.toString();
    }
}
