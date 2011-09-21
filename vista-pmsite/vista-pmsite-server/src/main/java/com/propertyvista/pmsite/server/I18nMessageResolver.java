/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.WicketTag;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.markup.resolver.IComponentResolver;

@SuppressWarnings("serial")
public class I18nMessageResolver implements IComponentResolver {

    private static final String TAG = "i18n";

    static {
        WicketTagIdentifier.registerWellKnownTagName(TAG);
    }

    @Override
    public boolean resolve(MarkupContainer container, MarkupStream markupStream, ComponentTag tag) {
        if (tag instanceof WicketTag) {
            WicketTag wtag = (WicketTag) tag;
            if (TAG.equalsIgnoreCase(wtag.getName())) {
                MessageContainer label = new MessageContainer("_i18n_" + container.getPage().getAutoIndex());
                label.setRenderBodyOnly(container.getApplication().getMarkupSettings().getStripWicketTags());
                container.autoAdd(label, markupStream);
                // Yes, we handled the tag
                return true;
            }
        }
        return false;
    }

    private static class MessageContainer extends MarkupContainer {

        public MessageContainer(String id) {
            super(id);
        }

        @Override
        protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
            renderComponentTagBody(markupStream, openTag);
        }

        @Override
        public boolean isTransparentResolver() {
            return true;
        }
    }

}
