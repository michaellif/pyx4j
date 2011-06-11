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
 * Created on Jun 11, 2011
 * @author Dad
 * @version $Id$
 */
package com.pyx4j.svg.common;

import java.util.HashMap;
import java.util.Map;

public class Animator {
    public enum Type {
        set, animateMotion, animate, animateTransform, animateColor
    }

    private final Type tag;

    private Map<String, String> attributes;

    public Animator(Type tag) {
        this.tag = tag;
        setattributes(null);
    }

    public Animator(Type tag, Map<String, String> attributes) {
        this.tag = tag;
        setattributes(attributes);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        setattributes(attributes);
    }

    public void setAttribute(String name, String value) {
        attributes.put(name, value);
    }

    public Type getTag() {
        return tag;
    }

    public String getTagName() {
        return tag.name();
    }

    private void setattributes(Map<String, String> attributes) {
        if (attributes == null)
            this.attributes = new HashMap<String, String>(5);
        else
            this.attributes = attributes;
    }

}
