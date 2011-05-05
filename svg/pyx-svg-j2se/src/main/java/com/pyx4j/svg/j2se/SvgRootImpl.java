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
 * Created on May 4, 2011
 * @author vadims
 * @version $Id$
 */
package com.pyx4j.svg.j2se;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGIDGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.SvgElement;
import com.pyx4j.svg.basic.SvgRoot;

public class SvgRootImpl implements SvgRoot {
    public static final String SVG_ID_PREFIX = "pyx-svg-";

    public static final String SVG_NAMESPACE = SVGDOMImplementation.SVG_NAMESPACE_URI;

    private static final SVGIDGenerator ID_GENERATOR = new SVGIDGenerator();

    private final String id;

    private final Document document;

    private final Element rootnode;

    public SvgRootImpl() {
        id = createUniqueId();
        document = SVGDOMImplementation.getDOMImplementation().createDocument(SVG_NAMESPACE, "svg", null);
        rootnode = document.getDocumentElement();
        setAttributeNS(null, "id", id);
    }

    @Override
    public void add(SvgElement element) {
        rootnode.appendChild(((ShapeImpl) element).getElement());
    }

    @Override
    public String getId() {
        return id;
    }

    public static String createUniqueId() {
        return ID_GENERATOR.generateID(SVG_ID_PREFIX);
    }

    public Document getDocument() {
        return document;
    }

    public Element getRootNode() {
        return rootnode;
    }

    public void setAttributeNS(String attr0, String attr1, String attr2) {
        rootnode.setAttributeNS(attr0, attr1, attr2);
    }

    @Override
    public void add(IsSvgElement element) {
        rootnode.appendChild(((ContainerElementImpl) element.asSvgElement()).getElement());
    }
}
