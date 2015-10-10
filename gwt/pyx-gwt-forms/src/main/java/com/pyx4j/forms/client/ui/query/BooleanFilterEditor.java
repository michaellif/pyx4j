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

import java.util.Arrays;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.query.IBooleanCondition;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.CheckGroup;
import com.pyx4j.widgets.client.OptionGroup.Layout;

public class BooleanFilterEditor extends FilterEditorBase<IBooleanCondition> {

    private static final I18n i18n = I18n.get(MultiSelectFilterEditor.class);

    private CheckGroup<Boolean> booleanGroup;

    public BooleanFilterEditor(IBooleanCondition condition) {
        super(condition);

        booleanGroup = new CheckGroup<>(Layout.HORIZONTAL);
        booleanGroup.setFormatter(new IFormatter<Boolean, SafeHtml>() {

            @Override
            public SafeHtml format(Boolean value) {
                String title;
                if (value) {
                    title = i18n.tr("Yes");
                } else {
                    title = i18n.tr("No");
                }
                return SafeHtmlUtils.fromTrustedString(title);
            }
        });
        booleanGroup.setOptions(Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.TRUE }));

        initWidget(booleanGroup);

    }

    @Override
    public void populate() {
        booleanGroup.setValue(getCondition().booleanValue().isNull() ? Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.TRUE })
                : Arrays.asList(new Boolean[] { getCondition().booleanValue().getValue() }));
    }

    @Override
    public void save() {
        getCondition().booleanValue().setValue((booleanGroup.getValue().size() != 1) ? null : booleanGroup.getValue().toArray(new Boolean[0])[0]);
    }

    @Override
    public void onShown() {
        super.onShown();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                booleanGroup.setFocus(true);
            }
        });
    }

}