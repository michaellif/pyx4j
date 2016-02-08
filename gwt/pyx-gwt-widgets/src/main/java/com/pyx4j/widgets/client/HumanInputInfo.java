/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Feb 8, 2016
 * @author vlads
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.HumanInputEvent;

public class HumanInputInfo {

    private final boolean controlKeyDown;

    private final boolean altKeyDown;

    private final boolean shiftKeyDown;

    public static final HumanInputInfo robot = new HumanInputInfo();

    public HumanInputInfo() {
        controlKeyDown = false;
        altKeyDown = false;
        shiftKeyDown = false;
    }

    public HumanInputInfo(HumanInputEvent<?> event) {
        controlKeyDown = event.isControlKeyDown();
        altKeyDown = event.isAltKeyDown();
        shiftKeyDown = event.isShiftKeyDown();
    }

    public HumanInputInfo(NativeEvent event) {
        controlKeyDown = event.getCtrlKey();
        altKeyDown = event.getAltKey();
        shiftKeyDown = event.getShiftKey();
    }

    /**
     * Is <code>alt</code> key down.
     *
     * @return whether the alt key is down
     */
    public boolean isAltKeyDown() {
        return altKeyDown;
    }

    /**
     * Is <code>control</code> key down.
     *
     * @return whether the control key is down
     */
    public boolean isControlKeyDown() {
        return controlKeyDown;
    }

    /**
     * Is <code>shift</code> key down.
     *
     * @return whether the shift key is down
     */
    public boolean isShiftKeyDown() {
        return shiftKeyDown;
    }
}
