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
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.webstorage.client.HTML5Storage;
import com.pyx4j.webstorage.client.StorageEvent;
import com.pyx4j.webstorage.client.StorageEventHandler;

public class HTML5StorageDemoPanel extends VerticalPanel {

    private static final Logger log = LoggerFactory.getLogger(HTML5StorageDemoPanel.class);

    public HTML5StorageDemoPanel() {

        final Button addListeners = new Button("Add Storage Listeners");
        this.add(addListeners);

        addListeners.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addListeners();
                addListeners.setEnabled(false);
            }
        });

    }

    private void addListeners() {
        HTML5Storage.getLocalStorage().addStorageEventHandler(new StorageEventHandler() {
            @Override
            public void onOptionsChange(StorageEvent event) {
                log.debug("Storage Event {} at {} ", event.getKey(), event.getUrl());
                log.debug("Storage Event {} -> {} ", event.getOldValue(), event.getNewValue());
            }
        });
        log.debug("LocalStorage StorageEventHandler registered");
    }
}
