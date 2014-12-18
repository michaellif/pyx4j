/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Mar 11, 2014
 * @author michaellif
 */
package com.pyx4j.site.client.ui.devconsole;

import java.util.Iterator;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.INativeComponent;
import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.resources.SiteImages;
import com.pyx4j.widgets.client.Button;

public abstract class AbstractDevConsole extends FlowPanel {

    private final Image deviceImage;

    protected abstract void setMockValues();

    public AbstractDevConsole() {

        getElement().getStyle().setPadding(10, Unit.PX);

        deviceImage = new Image();
        SimplePanel deviceImageHolder = new SimplePanel(deviceImage);
        deviceImageHolder.getElement().getStyle().setFloat(Float.RIGHT);
        deviceImageHolder.getElement().getStyle().setProperty("padding", "5px");
        deviceImageHolder.getElement().getStyle().setProperty("marginRight", "15px");
        deviceImageHolder.getElement().getStyle().setProperty("borderRadius", "5px");
        deviceImageHolder.getElement().getStyle().setProperty("background", "white");

        add(deviceImageHolder);

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    private void doLayout(LayoutType layoutType) {
        if (ApplicationMode.isDevelopment()) {
            switch (layoutType) {
            case phonePortrait:
                deviceImage.setResource(SiteImages.INSTANCE.phone());
                break;
            case phoneLandscape:
                deviceImage.setResource(SiteImages.INSTANCE.phoneL());
                break;
            case tabletPortrait:
                deviceImage.setResource(SiteImages.INSTANCE.tablet());
                break;
            case tabletLandscape:
                deviceImage.setResource(SiteImages.INSTANCE.tabletL());
                break;
            case monitor:
                deviceImage.setResource(SiteImages.INSTANCE.monitor());
                break;
            case huge:
                deviceImage.setResource(SiteImages.INSTANCE.huge());
                break;
            }
        }
    }

    protected void setMockValues(IsWidget widget) {

        if (widget instanceof INativeComponent) {
            CComponent<?, ?, ?, ?> component = ((INativeComponent<?>) widget).getCComponent();
            component.generateMockData();
        }

        if (widget instanceof HasWidgets) {
            for (Iterator<Widget> iterator = ((HasWidgets) widget).iterator(); iterator.hasNext();) {
                setMockValues(iterator.next());
            }
        }
    }

    public class SetMocksButton extends Button {
        public SetMocksButton() {
            super("Set Mock Values", new Command() {

                @Override
                public void execute() {
                    setMockValues();
                }
            });

            addAttachHandler(new AttachEvent.Handler() {
                private HandlerRegistration handlerRegistration;

                @Override
                public void onAttachOrDetach(AttachEvent event) {
                    if (event.isAttached()) {
                        handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                            @Override
                            public void onPreviewNativeEvent(NativePreviewEvent event) {
                                if ((event.getTypeInt() == Event.ONKEYDOWN && event.getNativeEvent().getCtrlKey())) {
                                    if (event.getNativeEvent().getKeyCode() == 'Q') {
                                        click();
                                        event.getNativeEvent().preventDefault();
                                    }
                                }
                            }
                        });
                    } else {
                        handlerRegistration.removeHandler();
                    }
                }
            });

            getElement().getStyle().setProperty("marginRight", "15px");
        }
    }
}
