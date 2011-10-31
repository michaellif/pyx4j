/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.model;

import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.resource.TextTemplateResourceReference;

public class WicketUtils {
    /*
     * add/remove single class from component class attribute
     */
    public static class AttributeClassModifier extends AttributeModifier {
        private static final long serialVersionUID = 1L;

        private final String find;

        public AttributeClassModifier(String find, String replace) {
            super("class", new Model<String>(replace));
            this.find = find;
        }

        @Override
        protected String newValue(final String curVal, final String repVal) {
            if (curVal == null || curVal.equals("")) {
                return repVal;
            } else if (find == null || find.equals("")) {
                return curVal + " " + repVal;
            }
            StringBuffer newVal = new StringBuffer();
            String[] valArr = curVal.split(" ");
            for (int i = 0; i < valArr.length; i++) {
                newVal.append(i > 0 ? " " : "");
                if (valArr[i].equals(find)) {
                    newVal.append(repVal == null ? "" : repVal);
                } else {
                    newVal.append(valArr[i]);
                }
            }
            return newVal.toString();
        }
    }

    /*
     * Stateless Selector with keys as values and default choice switch
     */
    public static class DropDownList<T> extends DropDownChoice<T> {
        private static final long serialVersionUID = 1L;

        final boolean useDefault;

        final boolean useKeys;

        public DropDownList(String id, List<? extends T> choices, final boolean useKeys, final boolean useDefault) {
            super(id, choices, new IChoiceRenderer<T>() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getDisplayValue(T param) {
                    return param.toString();
                }

                @Override
                public String getIdValue(T param, int paramInt) {
                    return useKeys ? String.valueOf(paramInt) : getDisplayValue(param);
                }

            });
            this.useDefault = useDefault;
            this.useKeys = useKeys;
        }

        @Override
        protected CharSequence getDefaultChoice(final String selected) {
            return useDefault ? super.getDefaultChoice(selected) : "";
        }

        @SuppressWarnings("unchecked")
        @Override
        protected T convertChoiceIdToChoice(String id) {
            if (useKeys) {
                // do default key-value conversion 
                return super.convertChoiceIdToChoice(id);
            } else {
                // just return the key, as we used it as value
                return (T) id;
            }
        }

        @Override
        public boolean getStatelessHint() {
            return true;
        }
    }

    /*
     * Stateless non-resource (external) image
     */
    public static class SimpleImage extends Image {
        private static final long serialVersionUID = 1L;

        public SimpleImage(String id, String url) {
            super(id);
            add(AttributeModifier.replace("src", url));
        }

        @Override
        public boolean getStatelessHint() {
            return true;
        }
    }

    public static class SimpleRadio<T> extends Radio<T> {
        private static final long serialVersionUID = 1L;

        public SimpleRadio(String id, IModel<T> model) {
            super(id, model);
        }

        @Override
        public boolean getStatelessHint() {
            return true;
        }

        @Override
        public String getValue() {
            return getParent().getId() + ":" + getId();
        }

    }

    /*
     * TextTemplateResource is auto-registered in ResourceReferenceRegistry cache
     * by the ResourceReferenceRegistry constructor, if not exist, so no changes will
     * be visible after the app is loaded for the first time.
     * We need to remove and re-register it again to pickup possible changes from CRM
     * in sub-sequential requests.
     * NOTE: The Key construction is taken from the ResourceReference.java.
     * It should match the original key used by the TextTemplateResourceReference
     */
    public static class VolatileTemplateResourceReference extends TextTemplateResourceReference {
        private static final long serialVersionUID = 1L;

        public VolatileTemplateResourceReference(Class<?> scope, String fileName, String contentType, IModel<Map<String, Object>> vars) {
            super(scope, fileName, contentType, vars);
            Key rcKey = new Key(scope.getName(), fileName, null, null, null);
            WebApplication.get().getResourceReferenceRegistry().unregisterResourceReference(rcKey);
            WebApplication.get().getResourceReferenceRegistry().registerResourceReference(this);

        }
    }

    /*
     * ActionLink is a version of a StatelessLink that is intended for running
     * a javascript action on the client with sub-sequential page reload.
     */
    public static class JSActionLink extends StatelessLink<Void> {
        private static final long serialVersionUID = 1L;

        public <C extends Page> JSActionLink(final String id, final String jsAction, final boolean reload) {
            super(id);
            String onClick = jsAction;
            onClick += ";" + (reload ? "window.location.reload()" : "return false");
            super.add(AttributeModifier.replace("onClick", onClick));
        }

        @Override
        public final void onClick() {
        }

        @Override
        protected CharSequence getURL() {
            return "";
        }
    }
}
