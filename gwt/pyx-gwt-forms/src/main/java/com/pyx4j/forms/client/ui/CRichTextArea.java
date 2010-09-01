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
 * Created on Jun 4, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.gwt.NativeRichTextArea;
import com.pyx4j.forms.client.gwt.NativeRichTextAreaPopup;

public class CRichTextArea extends CEditableComponent<String> {

    private INativeEditableComponent<String> nativeTextArea;

    private final int rows = 4;

    private final boolean popup;

    private IRichTextTidy tidy;

    public CRichTextArea() {
        this(false);
    }

    public CRichTextArea(boolean popup) {
        super();
        this.popup = popup;
        setWidth("100%");
    }

    public CRichTextArea(String title, boolean popup) {
        super(title);
        this.popup = popup;
        setWidth("100%");
    }

    @Override
    public INativeEditableComponent<String> getNativeComponent() {
        return nativeTextArea;
    }

    public void setTidy(IRichTextTidy tidy) {
        this.tidy = tidy;
    }

    public IRichTextTidy getTidy() {
        return tidy;
    }

    @Override
    public INativeEditableComponent<String> initNativeComponent() {
        if (nativeTextArea == null) {
            if (popup) {
                nativeTextArea = new NativeRichTextAreaPopup(this);
            } else {
                nativeTextArea = new NativeRichTextArea(this);
            }
            applyAccessibilityRules();
            setNativeComponentValue(getValue());
        }
        return nativeTextArea;
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || CommonsStringUtils.isEmpty(getValue());
    }

    public void scrollToBottom() {
        if (nativeTextArea != null && nativeTextArea instanceof NativeRichTextArea) {
            ((NativeRichTextArea) nativeTextArea).scrollToBottom();
        }
    }

    public int getRows() {
        return rows;
    }

}