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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.gwt.NativeTextArea;

public class CTextArea extends CEditableComponent<String> {

    private NativeTextArea nativeTextArea;

    private int rows = 4;

    public CTextArea() {
        super();
        setWidth("100%");
    }

    public CTextArea(String title) {
        super(title);
    }

    @Override
    public NativeTextArea getNativeComponent() {
        return nativeTextArea;
    }

    @Override
    public NativeTextArea initNativeComponent() {
        if (nativeTextArea == null) {
            nativeTextArea = new NativeTextArea(this);
            applyAccessibilityRules();
            setRows(rows);
            setNativeComponentValue(getValue());
        }
        return nativeTextArea;
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || CommonsStringUtils.isEmpty(getValue());
    }

    public void scrollToBottom() {
        if (nativeTextArea != null) {
            nativeTextArea.scrollToBottom();
        }
    }

    public void setRows(int rows) {
        this.rows = rows;
        if (nativeTextArea != null) {
            nativeTextArea.setVisibleLines(rows);
        }
    }

    public int getRows() {
        return rows;
    }

}
