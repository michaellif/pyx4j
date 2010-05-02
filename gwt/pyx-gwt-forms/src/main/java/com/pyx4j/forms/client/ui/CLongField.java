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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

public class CLongField extends CNumberField<Long> {

    public CLongField(String title) {
        super(title, "Should be numeric in range from " + Long.MIN_VALUE + " to " + Long.MAX_VALUE);
    }

    @Override
    boolean isInRange(Long value, Long from, Long to) {
        if (value == null) {
            return false;
        }
        return value >= from && value <= to;
    }

    @Override
    public Long valueOf(String string) {
        return Long.valueOf(string);
    }

}
