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

import com.pyx4j.entity.shared.IFile;
import com.pyx4j.i18n.shared.I18n;

public abstract class CFile<E extends IFile> extends CField<E, INativeHyperlink<E>> {

    private static final I18n i18n = I18n.get(CFile.class);

    private IFormat<E> format;

    public CFile() {
        this(null);
    }

    public CFile(Command command) {
        super("");

        setNativeWidget(new NFile<E>(this));

        setNavigationCommand(command);

        setFormat(new IFormat<E>() {
            @Override
            public String format(E value) {
                return value.fileName().getStringView();
            }

            @Override
            public E parse(String string) {
                return getValue();
            }
        });

    }

    public void setFormat(IFormat<E> format) {
        this.format = format;
    }

    public IFormat<E> getFormat() {
        return format;
    }

    public abstract void showFileSelectionDialog();
}