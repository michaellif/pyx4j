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

public abstract class CFocusComponent<E extends INativeFocusComponent> extends CComponent<E> {

    private int tabIndex = 0;

    public CFocusComponent() {
        this(null);
    }

    public CFocusComponent(String title) {
        super(title);
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
        if (getNativeComponent() != null) {
            getNativeComponent().setTabIndex(tabIndex);
        }
    }

    public void setFocus(boolean focused) {
        if (getNativeComponent() != null) {
            getNativeComponent().setFocus(focused);
        }
    }
}
