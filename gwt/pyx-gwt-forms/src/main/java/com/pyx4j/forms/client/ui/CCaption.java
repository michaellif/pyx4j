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
 * Created on Dec 3, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;

public class CCaption extends CEditableComponent<String, NativeCaption> implements SelfManagedComponent {

    private final String caption;

    public CCaption(String caption) {
        this.caption = caption;
        setWidth("100%");
    }

    @Override
    protected NativeCaption createWidget() {
        NativeCaption nativeCaption = new NativeCaption(this);
        nativeCaption.setWordWrap(true);
        nativeCaption.setText(caption);
        nativeCaption.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        nativeCaption.getElement().getStyle().setPaddingTop(10, Unit.PX);
        nativeCaption.getElement().getStyle().setPaddingBottom(4, Unit.PX);
        return nativeCaption;
    }

}