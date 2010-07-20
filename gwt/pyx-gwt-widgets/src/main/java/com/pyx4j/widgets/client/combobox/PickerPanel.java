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
 * Created on Jul 20, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.combobox;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

public abstract class PickerPanel<E> {

    private Widget widget;

    private OptionsGrabber<E> optionsGrabber;

    protected Widget getWidget() {
        return widget;
    }

    protected void setWidget(Widget widget) {
        this.widget = widget;
    }

    protected void setOptionsGrabber(OptionsGrabber<E> optionsGrabber) {
        this.optionsGrabber = optionsGrabber;
    }

    public OptionsGrabber<E> getOptionsGrabber() {
        return optionsGrabber;
    }

    protected abstract void setOptions(List<E> options);
}
