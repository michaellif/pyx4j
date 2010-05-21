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
 * Created on 2010-05-21
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.widgets.client.dialog.MessageDialog;

public class ConfirmActionClickHandler implements ClickHandler {

    private final String title;

    private final String text;

    final Runnable onConfirmed;

    public ConfirmActionClickHandler(String title, String text, final Runnable onConfirmed) {
        this.title = title;
        this.text = text;
        this.onConfirmed = onConfirmed;
    }

    @Override
    public void onClick(ClickEvent event) {
        MessageDialog.confirm(title, text, onConfirmed);
    }

}
