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

import com.google.gwt.user.client.ui.Hidden;

/**
 * Hidden component to hold the value
 */
public class CEditableHiddenValueComponent<E> extends CEditableComponent<E> {

    private INativeEditableComponent<E> nativeComponent;

    public CEditableHiddenValueComponent() {
        super();
    }

    public CEditableHiddenValueComponent(String title) {
        super(title);
    }

    @Override
    public INativeEditableComponent<E> getNativeComponent() {
        return nativeComponent;
    }

    @Override
    public INativeEditableComponent<E> initNativeComponent() {
        if (nativeComponent == null) {
            nativeComponent = new HiddenNativeEditableComponent<E>(this);
        }
        return nativeComponent;
    }

    private static class HiddenNativeEditableComponent<E> extends Hidden implements INativeEditableComponent<E> {

        private final CEditableComponent<E> owner;

        boolean readOnly;

        HiddenNativeEditableComponent(CEditableComponent<E> owner) {
            this.owner = owner;
        }

        @Override
        public void setReadOnly(boolean readOnly) {
            this.readOnly = readOnly;
        }

        @Override
        public boolean isReadOnly() {
            return readOnly;
        }

        @Override
        public void setNativeValue(E value) {
        }

        @Override
        public void setFocus(boolean focused) {
        }

        @Override
        public void setTabIndex(int tabIndex) {
        }

        @Override
        public CComponent<?> getCComponent() {
            return owner;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void setEnabled(boolean enabled) {
        }

    }

}
