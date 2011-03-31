package com.pyx4j.widgets.client.datepicker;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

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

                DOM.setCapture(HoldableImage.this.getElement());

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

        this.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                DOM.releaseCapture(HoldableImage.this.getElement());
                timer.cancel();
                change = 1;
                count = 0;
            }
        });
    }

    public HandlerRegistration addHoldElapsedHandler(HoldElapsedEventHandler handler) {
        return handlerManager.addHandler(TYPE, handler);
    }
}
