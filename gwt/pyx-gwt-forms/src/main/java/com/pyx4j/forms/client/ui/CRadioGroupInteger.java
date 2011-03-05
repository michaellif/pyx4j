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
 * Created on Mar 4, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.Collection;
import java.util.Map;

public class CRadioGroupInteger extends CRadioGroup<Integer> {

    private final Map<Integer, String> options;

    public CRadioGroupInteger(CRadioGroup.Layout layout, Map<Integer, String> options) {
        super(layout);
        this.options = options;
        setFormat(new IFormat<Integer>() {

            @Override
            public String format(Integer value) {
                return CRadioGroupInteger.this.options.get(value);
            }

            @Override
            public Integer parse(String string) {
                return null;
            }
        });
    }

    @Override
    public Collection<Integer> getOptions() {
        return options.keySet();
    }

    @Override
    public String getOptionDebugId(Integer option) {
        return option.toString();
    }

}
