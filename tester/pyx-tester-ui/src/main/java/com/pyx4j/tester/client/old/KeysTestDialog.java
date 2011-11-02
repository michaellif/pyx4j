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
 * Created on Apr 30, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client.old;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.widgets.client.dialog.Custom1Option;
import com.pyx4j.widgets.client.dialog.Dialog;

public class KeysTestDialog {

    public static void show() {

        Dialog dialog = new Dialog("Keys Test", new Custom1Option() {

            @Override
            public String custom1Text() {
                return "Close";
            }

            @Override
            public boolean onClickCustom1() {
                return true;
            }

            @Override
            public IDebugId getCustom1DebugID() {
                return null;
            }

        });

        final Label log = new Label();

        VerticalPanel inputPanel = new VerticalPanel();

        HorizontalPanel panel = new HorizontalPanel();
        inputPanel.add(panel);

        TextBox text;
        panel.add(new Label("Enter"));
        panel.add(text = new TextBox());

        inputPanel.add(log);

        dialog.setBody(inputPanel);

        text.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                log.setText(log.getText() + "|" + event.getNativeKeyCode());
            }
        });

        dialog.show();
    }
}
