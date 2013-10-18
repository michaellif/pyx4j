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
 * Created on May 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client;

import java.util.Random;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog_v2;

public class DownloadFrame extends Frame {

    private static final I18n i18n = I18n.get(DownloadFrame.class);

    private final String downloadFrameElementId;

    public DownloadFrame(String url) {
        super();
        downloadFrameElementId = generateId();
        getElement().setId(downloadFrameElementId);
        setSize("0px", "0px");
        setVisible(false);
        sinkEvents(Event.ONLOAD);
        RootPanel.get().add(this);
        setUrl(url);
    }

    @Override
    public void onBrowserEvent(Event event) {
        if (DOM.eventGetType(event) == Event.ONLOAD) {
            unsinkEvents(Event.ONLOAD);
            DOM.eventCancelBubble(event, true);

            String errorMessage = getDownloadFrameContent();
            MessageDialog_v2.error(i18n.tr("Download Error"), errorMessage);

            RootPanel.get().remove(this);
        } else {
            super.onBrowserEvent(event);
        }
    }

    private native String getDownloadFrameContent() /*-{ 
                                                    return $doc.getElementById(this.@com.pyx4j.essentials.client.DownloadFrame::downloadFrameElementId).contentWindow.document.documentElement.innerHTML;
                                                    }-*/;

    private String generateId() {
        Random rnd = new Random();
        return "downloadFrame" + String.valueOf(rnd.nextInt(10)) + String.valueOf(rnd.nextInt(10)) + String.valueOf(rnd.nextInt(10));
    }

}
