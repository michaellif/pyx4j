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
 * Created on 2010-10-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.dnd.demo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.dnd.client.DnDAdapter;
import com.pyx4j.dnd.client.DragEndEvent;
import com.pyx4j.dnd.client.DragEndHandler;
import com.pyx4j.dnd.client.DragEnterEvent;
import com.pyx4j.dnd.client.DragEnterHandler;
import com.pyx4j.dnd.client.DragLeaveEvent;
import com.pyx4j.dnd.client.DragLeaveHandler;
import com.pyx4j.dnd.client.DragOverEvent;
import com.pyx4j.dnd.client.DragOverHandler;
import com.pyx4j.dnd.client.DragStartEvent;
import com.pyx4j.dnd.client.DragStartHandler;
import com.pyx4j.dnd.client.DropEvent;
import com.pyx4j.dnd.client.DropHandler;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.widgets.client.dialog.UnrecoverableErrorHandlerDialog;

public class DnDDemo implements EntryPoint {

    private static final Logger log = LoggerFactory.getLogger(DnDDemo.class);

    public DnDDemo() {
        UnrecoverableErrorHandlerDialog.register();
        ClientLogger.setDebugOn(true);
    }

    @Override
    public void onModuleLoad() {

        Image img1 = new Image("http://code.google.com/webtoolkit/logo-185x175.png");
        RootPanel.get().add(img1, 40, 30);

        final Image img = new Image("http://code.google.com/webtoolkit/logo-185x175.png");
        RootPanel.get().add(img, 240, 30);

        DnDAdapter dnd = new DnDAdapter(img);

        dnd.addDragEnterHandler(new DragEnterHandler() {

            @Override
            public void onDragEnter(DragEnterEvent event) {
                log.debug("{}", event);
            }
        });

        dnd.addDragOverHandler(new DragOverHandler() {

            long dragOverDebugTime;

            @Override
            public void onDragOver(DragOverEvent event) {
                if (dragOverDebugTime < System.currentTimeMillis()) {
                    log.debug("{}", event);
                    dragOverDebugTime = System.currentTimeMillis() + 3000;
                }
                event.preventDefault();
            }
        });

        dnd.addDragLeaveHandler(new DragLeaveHandler() {

            @Override
            public void onDragLeave(DragLeaveEvent event) {
                log.debug("{}", event);
            }
        });

        dnd.addDropHandler(new DropHandler() {

            @Override
            public void onDrop(DropEvent event) {
                log.debug("{}", event);
                event.preventDefault();
            }
        });

        // ------------

        dnd.addDragStartHandler(new DragStartHandler() {

            @Override
            public void onDragStart(DragStartEvent event) {
                log.debug("{}", event);
            }
        });

        dnd.addDragEndHandler(new DragEndHandler() {

            @Override
            public void onDragEnd(DragEndEvent event) {
                log.debug("{}", event);
            }
        });

    }
}
