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
 */
package com.pyx4j.widgets.client.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.IFormatter;

/*
 * http://exceptional-code.blogspot.ca/2011/07/coding-up-trie-prefix-tree.html
 * http://en.wikipedia.org/wiki/Trie
 */

public class MultyWordSuggestTrie<E> {

    private static final Logger log = LoggerFactory.getLogger(MultyWordSuggestTrie.class);

    private static final String WHITESPACE_STRING = " ";

    private final Collection<E> options;

    private final IFormatter<E, String> formatter;

    private final PrefixTrie trie;

    public MultyWordSuggestTrie(Collection<E> options, IFormatter<E, String> formatter) {
        this.options = options;
        this.formatter = formatter;
        this.trie = new PrefixTrie();
        fillTrie();
    }

    private void fillTrie() {
        Map<String, Collection<E>> desc = new HashMap<String, Collection<E>>();

        if (options != null) {
            for (E option : options) {
                String stringValue = formatter.format(option).toLowerCase();
                stringValue = stringValue.toLowerCase().replaceAll("[^\\w']+", " ");
                String[] tokens = stringValue.split(WHITESPACE_STRING);

                for (int i = 0; i < tokens.length; i++) {
                    if (desc.containsKey(tokens[i])) {
                        Collection<E> collection = desc.get(tokens[i]);
                        collection.add(option);
                    } else {
                        Collection<E> newList = new ArrayList<E>();
                        newList.add(option);
                        desc.put(tokens[i], newList);
                    }
                }
            }
        }

        for (String key : desc.keySet()) {
            this.trie.add(key, desc.get(key));
        }

        log.trace(trie.toString());
    }

    public Collection<E> getCandidates(String query) {
        query = query.toLowerCase().replaceAll("[^\\w']+", " ");

        String[] searchWords = query.split(WHITESPACE_STRING);

        Collection<E> candidates = new ArrayList<E>();

        for (int i = 0; i < searchWords.length; i++) {
            Collection<String> words = this.trie.getWordsWithPrefix(searchWords[i]);
            Collection<E> localCandidates = new ArrayList<E>();
            for (String word : words) {
                Collection<E> newCandidates = this.trie.getMatches(word);
                for (E newCandidate : newCandidates) {
                    if (!localCandidates.contains(newCandidate)) {
                        localCandidates.add(newCandidate);
                    }
                }
            }
            if (i == 0) {
                candidates.addAll(localCandidates);
            } else {
                candidates.retainAll(localCandidates);
            }
        }

        log.trace(candidates.toString());
        return candidates;
    }

    public class PrefixTrie {
        private final Node root;

        PrefixTrie() {
            this.root = new Node(null);
        }

        public void add(CharSequence word, Collection<E> matches) {
            this.root.addWord(word, 0, matches);
        }

        public boolean contains(CharSequence word) {
            return root.contains(word, 0);
        }

        public Collection<E> getMatches(CharSequence word) {
            Node node = root.findNode(word, 0);
            if (null == node) {
                return null;
            }
            return node.getMatches();
        }

        public Collection<String> getWordsWithPrefix(CharSequence word) {
            return root.getWordsWithPrefix(word, 0);
        }

        @Override
        public String toString() {
            return root.toString();
        }

        private class Node {

            private boolean isWord = false;

            private final Map<Character, Node> children;

            private Collection<E> matches;

            private int depth;

            protected Node(Node parent) {
                this.children = new HashMap<Character, Node>();
                this.matches = new ArrayList<E>();
                if (parent != null) {
                    depth = parent.depth + 1;
                } else {
                    depth = 0;
                }
            }

            protected Collection<E> getMatches() {
                return (null == this.matches) ? new ArrayList<E>() : this.matches;
            }

            void addWord(CharSequence word, int currentIndex, Collection<E> matches) {
                if ((null == word) || (word.equals(""))) {
                    return;
                }
                if (currentIndex == word.length()) {
                    this.isWord = true;
                    this.matches = matches;
                    return;
                }
                char current = word.charAt(currentIndex);
                Node child = children.get(current);
                if (child == null) {
                    child = new Node(this);
                    children.put(current, child);
                }
                child.addWord(word, currentIndex + 1, matches);
            }

            boolean contains(CharSequence word, int currentIndex) {
                if (word.equals("")) {
                    return false;
                }
                if (currentIndex == word.length()) {
                    return isWord;
                }
                Node child = children.get(word.charAt(currentIndex));
                if (child == null) {
                    return false;
                }
                return child.contains(word, currentIndex + 1);
            }

            Node findNode(CharSequence word, int currentIndex) {
                if (currentIndex == word.length()) {
                    return ((this.isWord) ? this : null);
                }
                Node child = children.get(word.charAt(currentIndex));
                if (child == null) {
                    return null;
                } else
                    return child.findNode(word, currentIndex + 1);
            }

            Collection<String> getWordsWithPrefix(CharSequence prefix, int currentIndex) {
                if (currentIndex == prefix.length()) {
                    List<String> result = new ArrayList<>();
                    StringBuilder buffer = new StringBuilder();
                    buffer.append(prefix);
                    fillWords(buffer, result);
                    return result;
                }
                Node child = children.get(prefix.charAt(currentIndex));
                if (child == null) {
                    return Collections.emptyList();
                }
                return child.getWordsWithPrefix(prefix, currentIndex + 1);
            }

            private void fillWords(StringBuilder prefix, List<String> result) {
                if (isWord) {
                    result.add(prefix.toString());
                }
                for (Map.Entry<Character, Node> entry : children.entrySet()) {
                    prefix.append(entry.getKey());
                    entry.getValue().fillWords(prefix, result);
                    prefix.deleteCharAt(prefix.length() - 1);
                }
            }

            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();

                String indent = "";
                for (int i = 0; i < depth; i++) {
                    indent = indent + " ";
                }

                for (Character key : children.keySet()) {
                    builder.append(indent).append(key).append(children.get(key).matches).append("\n").append(children.get(key)).append("\n");
                }
                return builder.toString();
            }
        }
    }
}
