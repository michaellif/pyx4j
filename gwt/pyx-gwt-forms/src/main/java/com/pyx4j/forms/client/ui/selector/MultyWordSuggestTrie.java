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
 * Created on Jul 17, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.commons.IFormatter;

public class MultyWordSuggestTrie<E> {

    private static final String WHITESPACE_STRING = " ";

    private final Collection<E> options;

    private final IFormatter<E, String> formatter;

    public MultyWordSuggestTrie(Collection<E> options, IFormatter<E, String> formatter) {
        this.options = options;
        this.formatter = formatter;
    }

    public List<E> getCandidates(String query) {
        query = query.toLowerCase().replaceAll("\\s+", " ");

        String[] searchWords = query.split(WHITESPACE_STRING);

        List<E> candidates = new ArrayList<E>();
        for (E option : options) {
            if (isCandidate(option, searchWords)) {
                candidates.add(option);
            }
        }

        return candidates;
    }

    private boolean isCandidate(E value, String[] searchWords) {
        String valueToString = formatter.format(value).toLowerCase();
        for (String string : searchWords) {
            if (!valueToString.matches(".*[^\\s]*" + string + ".*")) {
                return false;
            }
        }
        return true;
    }
}
