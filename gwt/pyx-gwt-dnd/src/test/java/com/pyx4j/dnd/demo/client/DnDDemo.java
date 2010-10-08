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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.dnd.client.DataTransfer;
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
import com.pyx4j.widgets.client.util.BrowserType;

public class DnDDemo implements EntryPoint {

    private static final Logger log = LoggerFactory.getLogger(DnDDemo.class);

    public DnDDemo() {
        UnrecoverableErrorHandlerDialog.register();
        ClientLogger.setDebugOn(true);
    }

    @Override
    public void onModuleLoad() {

        VerticalPanel src = new VerticalPanel();
        RootPanel.get().add(src, 40, 30);

        src.add(new Label("Drag This:"));
        src.add(new Anchor("pyx4j.com", "http://pyx4j.com"));
        Image img1 = new Image("http://code.google.com/webtoolkit/images/gwt-sm.png");
        src.add(img1);

        VerticalPanel target = new VerticalPanel();
        RootPanel.get().add(target, 240, 30);

        final Image img = new Image("http://code.google.com/webtoolkit/logo-185x175.png");
        target.add(img);
        final Label lable = new Label();
        target.add(lable);

        DnDAdapter.addDragEnterHandler(img, new DragEnterHandler() {

            @Override
            public void onDragEnter(DragEnterEvent event) {
                log.debug("{}", event.toDebugString());
                lable.setText("-- DragEnter --");
            }
        });

        DnDAdapter.addDragOverHandler(img, new DragOverHandler() {

            long dragOverDebugTime;

            @Override
            public void onDragOver(DragOverEvent event) {
                if (dragOverDebugTime < System.currentTimeMillis()) {
                    log.debug("{}", event.toDebugString());
                    dragOverDebugTime = System.currentTimeMillis() + 3000;
                }
                //event.getDataTransfer().setDropEffect(DropEffect.copy);
                event.preventDefault();
            }
        });

        DnDAdapter.addDragLeaveHandler(img, new DragLeaveHandler() {

            @Override
            public void onDragLeave(DragLeaveEvent event) {
                log.debug("{}", event.toDebugString());
                lable.setText(null);
            }
        });

        DnDAdapter.addDropHandler(img, new DropHandler() {

            @Override
            public void onDrop(DropEvent event) {
                log.debug("{}", event.toDebugString());
                try {
                    if (event.getDataTransfer() != null) {
                        if (BrowserType.isIE()) {
                            String t = event.getDataTransfer().getData("Text");
                            if (t == null) {
                                t = event.getDataTransfer().getData("URL");
                            }
                            lable.setText(t);
                        } else {
                            lable.setText(event.getDataTransfer().getData(DataTransfer.TYPE_TEXT));
                        }
                    } else {
                        lable.setText("--no data--");
                    }
                } catch (Throwable e) {
                    lable.setText(e.getMessage());
                }
                event.preventDefault();
            }
        });

        // ------------

        DnDAdapter.addDragStartHandler(img, new DragStartHandler() {

            @Override
            public void onDragStart(DragStartEvent event) {
                log.debug("{}", event.toDebugString());
            }
        });

        DnDAdapter.addDragEndHandler(img, new DragEndHandler() {

            @Override
            public void onDragEnd(DragEndEvent event) {
                log.debug("{}", event.toDebugString());
            }
        });

    }
}
