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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class DomDebug {

    private static int attachedPanelsCount;

    private static int attachedWidgetCount;

    public static void printDomStats() {
        int count = getChildCount(RootPanel.get().getElement());
        //TODO Message.info("DOM Elements count:" + count + "; Attached panels:" + attachedPanelsCount + "; widgets:" + attachedWidgetCount);
    }

    private static int getChildCount(Element cElement) {
        int count = 1;
        cElement = cElement.getFirstChildElement();
        while (cElement != null) {
            count += getChildCount(cElement) + 1;
            cElement = cElement.getNextSiblingElement();
        }
        return count;
    }

    private static boolean collectGarbageManually = false;

    public static void collectGarbageManually() {
        collectGarbageManually = true;
        if (isCollectGarbagePresent()) {
            collectGarbageNative();
        }
    }

    public static void collectGarbage() {
        if (collectGarbageManually) {
            return;
        } else {
            if (isCollectGarbagePresent()) {
                collectGarbageNative();
            } else {
                collectGarbageManually = true;
            }
        }
    }

    /**
     * Force garbage collector in Internet Explorer.
     */
    private static native void collectGarbageNative() /*-{
        CollectGarbage();
    }-*/;

    public static native boolean isCollectGarbagePresent() /*-{
        return ("CollectGarbage" in $wnd)
    }-*/;

    public static void attachedPanel() {
        attachedPanelsCount++;
    }

    public static void detachPanel() {
        attachedPanelsCount--;
    }

    public static void attachedWidget() {
        attachedWidgetCount++;
    }

    public static void detachWidget() {
        attachedWidgetCount--;
    }
}
