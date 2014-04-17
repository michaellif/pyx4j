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

import java.text.ParseException;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;

public abstract class CViewer<E> extends CComponent<CViewer<E>, E> {

    private final NViewer<E> nativeComponent;

    public CViewer() {
        super();
        nativeComponent = new NViewer<E>(this);
    }

    @Override
    public final INativeComponent<E> getNativeComponent() {
        return nativeComponent;
    }

    public abstract IsWidget createContent(E value);

    class NViewer<DATA> extends SimplePanel implements INativeViewer<DATA> {

        private final CViewer<DATA> cComponent;

        private final SimplePanel contentPanel;

        public NViewer(CViewer<DATA> cComponent) {
            this.cComponent = cComponent;
            contentPanel = new SimplePanel();
            setWidget(contentPanel);
        }

        @Override
        public void setNativeValue(DATA value) {
            IsWidget widget = getCComponent().createContent(value);
            contentPanel.setWidget(widget);
        }

        @Override
        public void setEnabled(boolean enabled) {
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void setEditable(boolean editable) {
        }

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public CViewer<DATA> getCComponent() {
            return cComponent;
        }

        @Override
        public SimplePanel getContentHolder() {
            return this;
        }

        @Override
        public IsWidget getContent() {
            return contentPanel;
        }
    }

    @Override
    public Widget asWidget() {
        return nativeComponent;
    }

    @Override
    protected void setDebugId(IDebugId debugId) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setEditorValue(E value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected E getEditorValue() throws ParseException {
        // TODO Auto-generated method stub
        return null;
    }

}
