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
 *
 * Created on 2011-03-03
 * @author leont
 */

package com.pyx4j.widgets.client.datepicker;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.pyx4j.gwt.commons.ui.Image;

public class HoldableImage extends Image {

    public static final Type<HoldElapsedEventHandler> TYPE = new Type<HoldElapsedEventHandler>();

    public class HoldElapsedEvent extends GwtEvent<HoldElapsedEventHandler> {
        private final int change;

        public HoldElapsedEvent(int change) {
            this.change = change;
        }

        public int getChange() {
            return this.change;
        }

        @Override
        public Type<HoldElapsedEventHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(HoldElapsedEventHandler handler) {
            handler.onHoldElapsed(this);
        }
    }

    public interface HoldElapsedEventHandler extends EventHandler {
        void onHoldElapsed(HoldElapsedEvent event);
    }

    HandlerManager handlerManager;

    Timer timer;

    int change = 1;

    int count = 0;

    public HoldableImage(ImageResource resource, final int schedule) {
        super(resource);

        handlerManager = new HandlerManager(TYPE);

        this.addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent event) {
                timer = new Timer() {
                    @Override
                    public void run() {
                        handlerManager.fireEvent(new HoldElapsedEvent(change));
                        count++;
                        if (count % 4 == 0) {
                            change = change * 2;
                        }
                    }
                };
                timer.scheduleRepeating(schedule);
                timer.run();
            }
        });

        this.addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                stopHolding();
            }
        });

        this.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                stopHolding();
            }
        });
    }

    private void stopHolding() {
        if (timer != null) {
            timer.cancel();
            change = 1;
            count = 0;
        }
    }

    public HandlerRegistration addHoldElapsedHandler(HoldElapsedEventHandler handler) {
        return handlerManager.addHandler(TYPE, handler);
    }
}
