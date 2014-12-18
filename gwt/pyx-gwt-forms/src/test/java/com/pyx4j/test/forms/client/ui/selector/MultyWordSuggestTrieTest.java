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
 * Created on Jul 21, 2014
 * @author arminea
 */
package com.pyx4j.test.forms.client.ui.selector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.selector.MultyWordSuggestTrie;

public class MultyWordSuggestTrieTest {

    @Test
    public void testGetCandidates() {

        MultyWordSuggestTrie<String> suggestTrie;
        ArrayList<String> options = new ArrayList<String>();
        options.add("abcd, dcba");
        options.add("abcd abb ad dcba");
        options.add("abcd abb ad ddd");
        options.add("af. fbb");

        suggestTrie = new MultyWordSuggestTrie<String>(options, new IFormatter<String, String>() {
            @Override
            public String format(String value) {
                return value;
            }
        });

        Collection<String> candidates = suggestTrie.getCandidates("ab");
        assertEquals(3, candidates.size());

        assertTrue(candidates.contains("abcd, dcba"));
        assertTrue(candidates.contains("abcd abb ad dcba"));
        assertTrue(candidates.contains("abcd abb ad ddd"));

        candidates.clear();
        candidates.addAll(suggestTrie.getCandidates("abcd"));
        assertEquals(3, candidates.size());

        assertTrue(candidates.contains("abcd, dcba"));
        assertTrue(candidates.contains("abcd abb ad dcba"));
        assertTrue(candidates.contains("abcd abb ad ddd"));

        candidates.clear();
        candidates.addAll(suggestTrie.getCandidates("abcd ad"));
        assertEquals(2, candidates.size());

        assertFalse(candidates.contains("abcd, dcba"));
        assertTrue(candidates.contains("abcd abb ad dcba"));
        assertTrue(candidates.contains("abcd abb ad ddd"));
        assertFalse(candidates.contains("af. fbb"));

        candidates.clear();
        candidates.addAll(suggestTrie.getCandidates("af"));
        assertEquals(1, candidates.size());

        assertFalse(candidates.contains("abcd, dcba"));
        assertFalse(candidates.contains("abcd abb ad dcba"));
        assertFalse(candidates.contains("abcd abb ad ddd"));
        assertTrue(candidates.contains("af. fbb"));

    }
}
