/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jan 10, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.widgets.client.ITextWidget;
import com.pyx4j.widgets.client.WatermarkComponent;

public abstract class NTextComponent<DATA, WIDGET extends ITextWidget, CCOMP extends CTextComponent<DATA, ?>> extends
        NFocusComponent<DATA, WIDGET, CCOMP, HTML> implements INativeTextComponent<DATA>, WatermarkComponent {

    public NTextComponent(CCOMP cComponent) {
        super(cComponent);

    }

    @Override
    protected HTML createViewer() {
        return new HTML();
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    getCComponent().onEditingStop();
                }
            }
        });
        setWatermark(getCComponent().getWatermark());
    }

    @Override
    public String getNativeText() {
        if (!isViewable()) {
            return getEditor().getText();
        } else {
            return null;
        }
    }

    @Override
    public void setWatermark(String watermark) {
        if (getEditor() instanceof WatermarkComponent) {
            ((WatermarkComponent) getEditor()).setWatermark(watermark);
        }
    }
}