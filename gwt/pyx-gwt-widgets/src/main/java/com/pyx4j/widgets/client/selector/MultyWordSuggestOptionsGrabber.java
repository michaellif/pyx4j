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
 * Created on Jul 16, 2014
 * @author michaellif
 */
package com.pyx4j.widgets.client.selector;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.pyx4j.commons.IFormatter;

public class MultyWordSuggestOptionsGrabber<E> implements IOptionsGrabber<E> {

    private IFormatter<E, String> formatter;

    private Comparator<E> comparator = null;

    private MultyWordSuggestTrie<E> trie;

    public MultyWordSuggestOptionsGrabber() {
    }

    public void setFormatter(IFormatter<E, String> formatter) {
        this.formatter = formatter;
    }

    public void setAllOptions(Collection<E> options) {
        assert formatter != null;
        trie = new MultyWordSuggestTrie<E>(options, formatter);
    }

    public void setComparator(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void grabOptions(Request request, Callback<E> callback) {
        int limit = request.getLimit();

        // Get candidates from search words.
        Collection<E> candidates = trie.getCandidates(request.getQuery());

        // Respect limit for number of choices.
        for (int i = candidates.size() - 1; i > limit; i--) {
            candidates.remove(i);
        }

        if (candidates != null) {
            Collections.sort((List<E>) candidates, comparator);
        }

        Response<E> response = new Response<E>(candidates);

        callback.onOptionsReady(request, response);
    }

    @Override
    public SelectType getSelectType() {
        return SelectType.Multy;
    }

}
