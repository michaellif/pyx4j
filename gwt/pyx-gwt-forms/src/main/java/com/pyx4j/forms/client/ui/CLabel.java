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

import com.pyx4j.forms.client.gwt.NativeLabel;

public class CLabel extends CEditableComponent<String> {

    private NativeLabel nativeLabel;

    private boolean wordWrap = false;

    public CLabel() {
        this(null);
    }

    public CLabel(String title) {
        super(title);
        setWidth("200px");
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
    public INativeEditableComponent<String> getNativeComponent() {
        return nativeLabel;
    }

    @Override
    public INativeEditableComponent<String> initNativeComponent() {
        if (nativeLabel == null) {
            nativeLabel = new NativeLabel(this);
            setNativeComponentValue(getValue());
            applyAccessibilityRules();
        }
        return nativeLabel;
    }

}
