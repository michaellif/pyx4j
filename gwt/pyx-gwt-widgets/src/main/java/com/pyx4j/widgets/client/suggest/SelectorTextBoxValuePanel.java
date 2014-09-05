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
 * Created on Sep 4, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.suggest;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;

import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.TextBox;

public class SelectorTextBoxValuePanel<E> extends TextBox implements ISelectorValuePanel<E> {

    public SelectorTextBoxValuePanel() {
        setAction(new Command() {
            @Override
            public void execute() {

            }
        }, ImageFactory.getImages().action());
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return getTextBoxWidget().addClickHandler(handler);
    }

    @Override
    public String getQuery() {
        return getText();
    }

}
