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
 * Created on 2011-04-17
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.tester.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.dashboard.IGadget;

// define demo widget class: 
public class DemoGadget extends HTML implements IGadget {

    boolean fullWidth = true;

    public DemoGadget(String s) {
        super(s);
    }

    // info:

    @Override
    public String getName() {
        return (getText() + " name");
    }

    @Override
    public String getDescription() {
        return (getText() + " description");
    }

    // flags:

    @Override
    public boolean isMaximizable() {
        return true;
    }

    @Override
    public boolean isMinimizable() {
        return true;
    }

    @Override
    public boolean isSetupable() {
        return true;
    }

    @Override
    public boolean isFullWidth() {
        return fullWidth;
    }

    // setup:

    public void setFullWidth(boolean fullWidth) {
        this.fullWidth = fullWidth;
    }

    @Override
    public ISetup getSetup() {
        class MySetup implements ISetup {
            private final TextArea content = new TextArea();

            @Override
            public Widget asWidget() {
                FlowPanel setupPanel = new FlowPanel();
                setupPanel.add(new Label("Enter new gadget content:"));

                content.setText(getHTML());
                content.setWidth("100%");
                setupPanel.add(content);

                setupPanel.getElement().getStyle().setPadding(10, Unit.PX);
                setupPanel.getElement().getStyle().setPaddingBottom(0, Unit.PX);
                return setupPanel;
            }

            @Override
            public boolean onStart() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onOk() {
                setHTML(content.getText());
                return true;
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        }

        return new MySetup();
    }

    // notifications:

    @Override
    public void onMaximize(boolean maximized_restored) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMinimize(boolean minimized_restored) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDelete() {
        // TODO Auto-generated method stub

    }

    @Override
    public void start() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public void suspend() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }
}