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
 * Created on Feb 12, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex.editor;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IEntity;

public interface IFolderItemDecorator<E extends IEntity> extends IsWidget {

    HandlerRegistration addItemClickHandler(ClickHandler handler);

    HandlerRegistration addItemRemoveClickHandler(ClickHandler handler);

    HandlerRegistration addRowUpClickHandler(ClickHandler handler);

    HandlerRegistration addRowDownClickHandler(ClickHandler handler);

    HandlerRegistration addRowCollapseClickHandler(ClickHandler handler);

    void setFolderItem(CEntityFolderItemEditor<E> item);

}