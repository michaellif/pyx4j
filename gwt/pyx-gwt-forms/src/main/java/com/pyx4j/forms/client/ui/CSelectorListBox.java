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
 */
package com.pyx4j.forms.client.ui;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.widgets.client.selector.IOptionsGrabber;

public class CSelectorListBox<E extends IEntity> extends CAbstractSelectorBox<Collection<E>, E, NSelectorListBox<E>> {

    private Command addItemCommand;

    public CSelectorListBox(IOptionsGrabber<E> optionsGrabber) {
        super(optionsGrabber);
        setNativeComponent(new NSelectorListBox<E>(this));
    }

    public void setAddItemCommand(Command addItemCommand) {
        this.addItemCommand = addItemCommand;
    }

    public Command getAddItemCommand() {
        return addItemCommand;
    }

    @Override
    protected Collection<E> preprocessValue(Collection<E> value, boolean fireEvent, boolean populate) {
        return super.preprocessValue(new ArrayList<>(value), fireEvent, populate);
    }

    @Override
    public boolean isValueEmpty() {
        return getValue() == null || getValue().size() == 0;
    }

}
