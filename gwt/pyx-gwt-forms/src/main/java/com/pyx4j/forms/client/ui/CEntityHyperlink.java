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
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IEntity;

public class CEntityHyperlink<E extends IEntity> extends CEntityLabel<E> {

    protected CEntityHyperlink() {
        this(null);
    }

    public CEntityHyperlink(Command command) {
        super();
        setNavigationCommand(command);
    }

    /**
     * Allow presentation update of the same entity when setValue is called
     */
    @Override
    public boolean isValuesEquals(E value1, E value2) {
        return value1 == value2;
    }
}