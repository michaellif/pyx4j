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

import com.google.gwt.user.client.Command;

public class CAbstractHyperlink<E> extends CReference<E, NativeHyperlink<E>> {

    private boolean wordWrap = false;

    private final Command command;

    public CAbstractHyperlink(Command command) {
        this(null, command);
    }

    public CAbstractHyperlink(String title, Command command) {
        super(title);
        this.command = command;
    }

    @Override
    protected NativeHyperlink<E> createWidget() {
        return new NativeHyperlink<E>(this, command);
    }

    public void setWordWrap(boolean wrap) {
        if (isWidgetCreated()) {
            asWidget().setWordWrap(wrap);
        }
        wordWrap = wrap;
    }

    public boolean isWordWrap() {
        return wordWrap;
    }
}
