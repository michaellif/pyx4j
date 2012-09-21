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
 * Created on 2011-03-22
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.Command;

public abstract class CReference<E> extends CComponent<E, NReference<E>> {

    private boolean wordWrap = false;

    private boolean allowHtml = false;

    private IFormat<E> format;

    private Command command;

    public CReference(String title) {
        super(title);
    }

    public CReference(Command command) {
        this(null, command);
    }

    public CReference(String title, Command command) {
        super(title);
        this.command = command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    @Override
    protected NReference<E> createWidget() {
        NReference<E> widget = new NReference<E>(this);
        widget.setWordWrap(this.isWordWrap());
        return widget;
    }

    public void setFormat(IFormat<E> format) {
        this.format = format;
    }

    public IFormat<E> getFormat() {
        return format;
    }

    public boolean isWordWrap() {
        return wordWrap;
    }

    public void setWordWrap(boolean wrap) {
        if (isWidgetCreated()) {
            getWidget().setWordWrap(wrap);
        }
        wordWrap = wrap;
    }

    public boolean isAllowHtml() {
        return allowHtml;
    }

    public void setAllowHtml(boolean allowHtml) {
        this.allowHtml = allowHtml;
    }

    @Override
    public void onEditingStart() {
        // do nothing - not editable...
    }

    @Override
    public void onEditingStop() {
        // do nothing - not editable...
    }
}
