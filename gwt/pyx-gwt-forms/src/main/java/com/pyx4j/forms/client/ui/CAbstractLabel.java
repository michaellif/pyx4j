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
 * Created on Jun 12, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;


public class CAbstractLabel<E> extends CEditableComponent<E, NativeLabel<E>> {

    private boolean wordWrap = false;

    private boolean allowHtml = false;

    private IFormat<E> format;

    public CAbstractLabel() {
        this(null);
    }

    public CAbstractLabel(String title) {
        super(title);
        setWidth("100%");
    }

    public void setFormat(IFormat<E> format) {
        this.format = format;
    }

    public IFormat<E> getFormat() {
        return format;
    }

    public void setWordWrap(boolean wrap) {
        if (isWidgetCreated()) {
            asWidget().setWordWrap(wrap);
        }
        wordWrap = wrap;
    }

    public boolean isWordWrap() {
        return wordWrap;
    }

    @Override
    protected NativeLabel<E> createWidget() {
        NativeLabel<E> nativeLabel = createNativeLabel();
        nativeLabel.setWordWrap(this.isWordWrap());
        nativeLabel.setWidth(this.getWidth());
        return nativeLabel;
    }

    protected NativeLabel<E> createNativeLabel() {
        return new NativeLabel<E>(this);
    }

    public boolean isAllowHtml() {
        return allowHtml;
    }

    public void setAllowHtml(boolean allowHtml) {
        this.allowHtml = allowHtml;
    }

}
