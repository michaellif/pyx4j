/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Mar 25, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class GroupFocusHandler extends HandlerManager implements FocusHandler, BlurHandler, HasFocusHandlers, HasBlurHandlers {

    private boolean focusLost = true;

    private boolean groupFocus = false;

    private GroupFocusHandler parentGroupFocusHandler;

    public GroupFocusHandler(Object source) {
        super(source);
    }

    @Override
    public void onFocus(FocusEvent e) {
        if (parentGroupFocusHandler != null) {
            parentGroupFocusHandler.onFocus(e);
        } else {
            System.out.println("+++++++++++++++ onFocus " + System.identityHashCode(this));
            focusLost = false;
            if (!groupFocus) {
                groupFocus = true;
                fireEvent(e);
            }
        }
    }

    @Override
    public void onBlur(final BlurEvent e) {
        if (parentGroupFocusHandler != null) {
            parentGroupFocusHandler.onBlur(e);
        } else {
            System.out.println("+++++++++++++++ onBlur " + System.identityHashCode(this));
            focusLost = true;
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    if (groupFocus && focusLost) {
                        groupFocus = false;
                        fireEvent(e);
                    }
                }
            });
        }

    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        return addHandler(FocusEvent.getType(), focusHandler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        return addHandler(BlurEvent.getType(), blurHandler);
    }

    public void addFocusable(IFocusable focusable) {
        if (focusable instanceof IGroupFocus) {
            ((IGroupFocus) focusable).getGroupFocusHandler().setParentGroupFocusHandler(this);
        }

        focusable.addFocusHandler(this);
        focusable.addBlurHandler(this);

    }

    private void setParentGroupFocusHandler(GroupFocusHandler parentGroupFocusHandler) {
        this.parentGroupFocusHandler = parentGroupFocusHandler;
    }

}
