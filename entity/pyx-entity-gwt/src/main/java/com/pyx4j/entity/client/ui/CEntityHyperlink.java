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
 * Created on Apr 28, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CAbstractHyperlink;
import com.pyx4j.forms.client.ui.IFormat;

public class CEntityHyperlink<E extends IEntity> extends CAbstractHyperlink<E> {

    private final String uriPrefix;

    // set default formatting (kind of common constructor):
    {
        setWordWrap(true);
        this.setFormat(new IFormat<E>() {
            @Override
            public String format(E value) {
                if (value != null) {
                    return value.getStringView();
                } else {
                    return null;
                }
            }

            @Override
            public E parse(String string) {
                return null;
            }
        });
    }

    public CEntityHyperlink(Command command) {
        this(null, command);
    }

    public CEntityHyperlink(String title, Command command) {
        super(title, command);
        this.uriPrefix = null;
    }

    public CEntityHyperlink(String title, String uriPrefix) {
        super(title);
        this.uriPrefix = uriPrefix;
        setCommand(new Command() {
            @Override
            public void execute() {
                History.newItem(getEntityHistoryToken(getValue()));
            }
        });
    }

    protected String getEntityHistoryToken(IEntity value) {
        return uriPrefix + value.getPrimaryKey();
    }

    /**
     * Allow presentation update of the same entity when setValue is called
     */
    @Override
    public boolean isValuesEquals(E value1, E value2) {
        return value1 == value2;
    }
}