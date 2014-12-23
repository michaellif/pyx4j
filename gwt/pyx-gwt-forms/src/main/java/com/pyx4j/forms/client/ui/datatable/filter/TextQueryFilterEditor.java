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
 * Created on Dec 22, 2014
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.widgets.client.StringBox;

public class TextQueryFilterEditor extends FilterEditorBase implements IFilterEditor {

    private final StringBox queryBox;

    public TextQueryFilterEditor() {
        queryBox = new StringBox();
        initWidget(queryBox);
    }

    @Override
    public PropertyCriterion getPropertyCriterion() {
        if (queryBox.getValue() == null || queryBox.getValue().trim().equals("")) {
            return null;
        } else {
            return PropertyCriterion.like(getMemeber(), queryBox.getValue());
        }
    }

    @Override
    public void setPropertyCriterion(PropertyCriterion criterion) {
        if (criterion == null || criterion.getValue() == null) {
            queryBox.setValue(null);
        } else {
            if (criterion.getRestriction() != PropertyCriterion.Restriction.RDB_LIKE) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            if (!getMemeber().getPath().toString().equals(criterion.getPropertyPath())) {
                throw new Error("Filter editor member doesn't mach filter criterion path");
            }

            if (!(criterion.getValue() instanceof String)) {
                throw new Error("Filter criterion value class is" + criterion.getValue().getClass().getSimpleName() + ". String is expected.");
            }

            queryBox.setValue((String) criterion.getValue());
        }
    }

    @Override
    public void onShown() {
        super.onShown();
        queryBox.setFocus(true);
    }
}
