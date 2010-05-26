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

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.gwt.NativeForm;
import com.pyx4j.forms.client.ui.CGroupBoxPanel.Layout;

public class CForm extends CContainer {

    public enum LabelAlignment {
        LEFT, TOP;
    }

    public enum InfoImageAlignment {
        BEFORE, AFTER, HIDDEN;
    }

    private NativeForm nativeForm;

    private CComponent<?>[][] components;

    private final Collection<CComponent<?>> componentCollection = new ArrayList<CComponent<?>>();

    private final LabelAlignment allignment;

    private final InfoImageAlignment infoImageAlignment;

    private final CFormFolder folder;

    public CForm() {
        this(null, LabelAlignment.LEFT, InfoImageAlignment.AFTER);
    }

    public CForm(CFormFolder folder) {
        this(null, LabelAlignment.LEFT, InfoImageAlignment.AFTER, folder);
    }

    public CForm(LabelAlignment allignment) {
        this(null, allignment, InfoImageAlignment.AFTER);
    }

    public CForm(LabelAlignment allignment, CFormFolder folder) {
        this(null, allignment, InfoImageAlignment.AFTER, folder);
    }

    public CForm(String title) {
        this(title, LabelAlignment.LEFT, InfoImageAlignment.AFTER);
    }

    protected CForm(String title, LabelAlignment allignment, InfoImageAlignment infoImageAlignment, CFormFolder folder) {
        super(title);
        this.allignment = allignment;
        this.infoImageAlignment = infoImageAlignment;
        this.folder = folder;
    }

    public CForm(String title, LabelAlignment allignment, InfoImageAlignment infoImageAlignment) {
        this(title, allignment, infoImageAlignment, null);
    }

    public static Widget createFormWidget(LabelAlignment allignment, CComponent<?>[][] components) {
        CForm form = new CForm(allignment);
        form.setComponents(components);
        return (Widget) form.initNativeComponent();
    }

    public static Widget createDecoratedFormWidget(LabelAlignment allignment, CComponent<?>[][] components, String caption) {
        return createDecoratedFormWidget(allignment, components, caption, false, true);
    }

    public static Widget createDecoratedFormWidget(LabelAlignment allignment, CComponent<?>[][] components, String caption, boolean collapsible,
            boolean expended) {
        CGroupBoxPanel group = new CGroupBoxPanel(caption, collapsible);
        group.setExpended(expended);
        CForm form = new CForm(allignment);
        form.setComponents(components);
        group.addComponent(form);
        return (Widget) group.initNativeComponent();
    }

    public static Widget createToggleFormWidget(LabelAlignment allignment, CComponent<?>[][] components, String caption, boolean expended) {
        CGroupBoxPanel group = new CGroupBoxPanel(caption, Layout.CHECKBOX_TOGGLE);
        group.setExpended(expended);
        CForm form = new CForm(allignment);
        form.setComponents(components);
        group.addComponent(form);
        return (Widget) group.initNativeComponent();
    }

    public void setComponents(CComponent<?>[][] components) {
        this.components = components;

        for (int i = 0; i < components.length; i++) {
            for (int j = 0; j < components[i].length; j++) {
                if (components[i][j] != null) {
                    if (componentCollection.contains(components[i][j])) {
                        continue;
                    }
                    componentCollection.add(components[i][j]);
                    components[i][j].setParent(this);
                }
            }
        }
    }

    @Override
    public NativeForm getNativeComponent() {
        return nativeForm;
    }

    public CFormFolder getFolder() {
        return folder;
    }

    @Override
    public INativeComponent initNativeComponent() {
        if (nativeForm == null) {
            nativeForm = new NativeForm(this, components, allignment, infoImageAlignment);
            applyAccessibilityRules();
        }
        return nativeForm;
    }

    @Override
    public void addComponent(CComponent<?> component) {
        throw new NotApplicableException();
    }

    @Override
    public Collection<CComponent<?>> getComponents() {
        return componentCollection;
    }

}
