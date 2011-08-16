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
package com.propertyvista.crm.client.ui.components;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CAbstractHyperlink;
import com.pyx4j.forms.client.ui.IFormat;

public class CListHyperlink extends CAbstractHyperlink<IList<?>> {

    private final String customTile;

    // set default formatting (kind of common constructor):
    {
        setWordWrap(true);
        this.setFormat(new IFormat<IList<?>>() {
            @Override
            public String format(IList<?> value) {
                return getTitle();
            }

            @Override
            public IList<?> parse(String string) {
                return null;
            }
        });
    }

    public CListHyperlink(Command command) {
        this(null, command);
    }

    public CListHyperlink(String title, Command command) {
        super(title, command);
        customTile = title;
    }

    @Override
    public String getTitle() {
        return (customTile == null ? super.getTitle() : customTile);
    }
}