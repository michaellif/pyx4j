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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.Model;

public class WicketUtils {
    /*
     * add/remove single class from component class attribute
     */
    public static class AttributeClassModifier extends AttributeModifier {
        private static final long serialVersionUID = 1L;

        private final String find;

        public AttributeClassModifier(String find, String replace) {
            super("class", true, new Model<String>(replace));
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
     * Selector with keys as values and default choice switch
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
        protected CharSequence getDefaultChoice(final Object selected) {
            return useDefault ? super.getDefaultChoice(selected) : "";
        }
    }
}
