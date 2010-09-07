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

import com.pyx4j.forms.client.gwt.NativeLabel;

public class CAbstractLabel<E> extends CEditableComponent<E> {

    private NativeLabel<E> nativeLabel;

    private boolean wordWrap = false;

    private boolean allowHtml = false;

    public CAbstractLabel() {
        this(null);
    }

    public CAbstractLabel(String title) {
        super(title);
        setWidth("100%");
    }

    public void setWordWrap(boolean wrap) {
        if (nativeLabel != null) {
            nativeLabel.setWordWrap(wrap);
        }
        wordWrap = wrap;
    }

    public boolean isWordWrap() {
        return wordWrap;
    }

    @Override
    public INativeEditableComponent<E> getNativeComponent() {
        return nativeLabel;
    }

    protected NativeLabel<E> createNativeLabel() {
        return new NativeLabel<E>(this);
    }

    @Override
    public INativeEditableComponent<E> initNativeComponent() {
        if (nativeLabel == null) {
            nativeLabel = createNativeLabel();
            nativeLabel.setWordWrap(this.isWordWrap());
            nativeLabel.setWidth(this.getWidth());
            setNativeComponentValue(getValue());
            applyAccessibilityRules();
        }
        return nativeLabel;
    }

    public boolean isAllowHtml() {
        return allowHtml;
    }

    public void setAllowHtml(boolean allowHtml) {
        this.allowHtml = allowHtml;
    }

}
