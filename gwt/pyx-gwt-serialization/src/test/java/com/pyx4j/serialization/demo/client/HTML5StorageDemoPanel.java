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
 * Created on Sep 11, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.demo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import com.pyx4j.webstorage.client.HTML5Storage;
import com.pyx4j.webstorage.client.StorageEvent;
import com.pyx4j.webstorage.client.StorageEventHandler;
import com.pyx4j.widgets.client.GroupBoxPanel;

public class HTML5StorageDemoPanel extends GroupBoxPanel {

    private static final Logger log = LoggerFactory.getLogger(HTML5StorageDemoPanel.class);

    private Label events;

    private int eventCount;

    private TextBox key;

    private TextBox value;

    private CheckBox sessionStorage;

    public HTML5StorageDemoPanel() {
        super(true);
        setCaption("Manual HTML5 Storage tests");

        HorizontalPanel panel = new HorizontalPanel();
        this.add(panel);

        panel.add(new Label("Key:"));
        panel.add(key = new TextBox());
        key.setValue("test1");
        panel.add(new Label("Value:"));
        panel.add(value = new TextBox());

        HorizontalPanel panelBtns = new HorizontalPanel();
        this.add(panelBtns);
        panelBtns.add(new Label("Use Session Storage:"));
        panelBtns.add(sessionStorage = new CheckBox());

        final Button btnSet = new Button("SetItem");
        panelBtns.add(btnSet);

        btnSet.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getSelectedStorage().setItem(key.getValue(), value.getValue());
            }
        });

        final Button btnGet = new Button("getItem");
        panelBtns.add(btnGet);

        btnGet.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                value.setValue(getSelectedStorage().getItem(key.getValue()));
            }
        });

        final Button btnRemove = new Button("removeItem");
        panelBtns.add(btnRemove);

        btnRemove.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getSelectedStorage().removeItem(key.getValue());
            }
        });

        final Button addListeners = new Button("Add Storage Listeners");
        this.add(addListeners);

        addListeners.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addListeners();
                addListeners.setEnabled(false);
            }
        });

        this.add(events = new Label("0 events"));
        events.setTitle("Count of Storage Event");
    }

    HTML5Storage getSelectedStorage() {
        if (sessionStorage.getValue()) {
            return HTML5Storage.getSessionStorage();
        } else {
            return HTML5Storage.getLocalStorage();
        }
    }

    private void addListeners() {
        HTML5Storage.getLocalStorage().addStorageEventHandler(new StorageEventHandler() {
            @Override
            public void onOptionsChange(StorageEvent event) {
                log.debug("Storage Event {} at {} ", event.getKey(), event.getUrl());
                log.debug("Storage Event {} -> {} ", event.getOldValue(), event.getNewValue());
                eventCount++;
                events.setText(eventCount + " events");
            }
        });
        log.debug("LocalStorage StorageEventHandler registered");
    }
}
