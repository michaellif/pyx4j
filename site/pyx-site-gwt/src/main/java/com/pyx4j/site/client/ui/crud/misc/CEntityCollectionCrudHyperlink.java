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
 * Created on Sep 17, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud.misc;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.forms.client.ui.CReference;
import com.pyx4j.forms.client.ui.IFormat;

public class CEntityCollectionCrudHyperlink<E extends ICollection<?, ?>> extends CReference<E> {

    protected CEntityCollectionCrudHyperlink(String title) {
        super(title);
        setWordWrap(true);
        setFormat(new IFormat<E>() {
            @Override
            public String format(E value) {
                if (value != null) {
                    return value.size() + "";
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

    public CEntityCollectionCrudHyperlink(String title, Command command) {
        this(title);
        setCommand(command);
    }

    /**
     * Allow presentation update of the same entity when setValue is called
     */
    @Override
    public boolean isValuesEquals(E value1, E value2) {
        return value1 == value2;
    }

}