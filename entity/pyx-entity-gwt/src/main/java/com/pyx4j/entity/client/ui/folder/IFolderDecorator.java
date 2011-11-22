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
 * Created on Feb 11, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.folder;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.IDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

public interface IFolderDecorator<E extends IEntity> extends IDecorator<CEntityFolder<E>>, ValueChangeHandler<IList<E>> {

    public static String DEBUGID_SUFIX = "_fd_";

    HandlerRegistration addItemAddClickHandler(ClickHandler handler);

    void setAddButtonVisible(boolean show);

    @Override
    void setComponent(CEntityFolder<E> folder);

    public enum DecoratorsIds implements IDebugId {
        Validation, Label;

        @Override
        public String debugId() {
            return name();
        }
    }

}