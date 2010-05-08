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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

public class DownloadFrame extends Frame {

    public DownloadFrame(String url) {
        super();
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
            RootPanel.get().remove(this);
        } else {
            super.onBrowserEvent(event);
        }
    }

}
