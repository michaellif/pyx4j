/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jun 8, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.forms.client.ui.query;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.i18n.shared.I18n;

public class CheckGroup<E> extends com.pyx4j.widgets.client.CheckGroup<E> {

    private static final I18n i18n = I18n.get(CheckGroup.class);

    public CheckGroup(Layout layout) {
        super(layout);
    }

    public void setAllSelected() {
        for (E item : getButtons().keySet()) {
            getButtons().get(item).setValue(Boolean.TRUE);
        }
    }

    public boolean isAllSelected() {
        for (E item : getButtons().keySet()) {
            if (!getButtons().get(item).getValue()) {
                return false;
            }
        }
        return true;
    }

    public void setEmptyFieldFormatter() {
        super.setFormatter(new IFormatter<E, SafeHtml>() {

            @Override
            public SafeHtml format(E value) {
                String title;
                if (value == null) {
                    title = i18n.tr("<i>Empty</i>");
                } else {
                    title = value.toString();
                }
                return SafeHtmlUtils.fromTrustedString(title);
            }
        });
    }
}
