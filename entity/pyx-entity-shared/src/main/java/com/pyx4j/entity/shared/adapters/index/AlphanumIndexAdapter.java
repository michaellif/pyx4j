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
 * Created on 2012-09-10
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.adapters.index;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.meta.MemberMeta;

public class AlphanumIndexAdapter extends AbstractIndexAdapter<String> {

    private static final int maxGroups = 2;

    private static final int paddingMax = 6;

    @Override
    public Object getIndexedValue(IEntity entity, MemberMeta memberMeta, String value) {
        if (value == null) {
            return null;
        } else {
            return alphanum(value);
        }
    }

    @Override
    public Class<?> getIndexValueClass() {
        return String.class;
    }

    @Override
    public int getIndexValueLength(MemberMeta memberMeta) {
        int len = super.getIndexValueLength(memberMeta);
        if (len == 0) {
            return 0;
        } else {
            return len + maxGroups * paddingMax;
        }
    }

    public static String alphanum(String value) {
        StringBuilder b = new StringBuilder();
        StringBuilder n = new StringBuilder();
        int padCount = 0;
        for (char c : value.toCharArray()) {
            if (padCount >= maxGroups) {
                b.append(Character.toLowerCase(c));
            } else if (Character.isDigit(c)) {
                n.append(c);
            } else {
                if (n.length() != 0) {
                    normalizenum(b, n);
                    padCount++;
                    n = new StringBuilder();
                }
                b.append(Character.toLowerCase(c));
            }
        }
        if (n.length() != 0) {
            normalizenum(b, n);
        }
        return b.toString();
    }

    public static void normalizenum(StringBuilder b, StringBuilder value) {
        for (int i = 0; i <= paddingMax - value.length(); i++) {
            b.append('0');
        }
        b.append(value);
    }
}
