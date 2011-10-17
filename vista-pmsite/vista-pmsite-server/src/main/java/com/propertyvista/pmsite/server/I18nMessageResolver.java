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

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.markup.resolver.IComponentResolver;

import com.pyx4j.i18n.shared.I18n;

@SuppressWarnings("serial")
public class I18nMessageResolver implements IComponentResolver {

    private static final String TAG = "i18n";

    static {
        WicketTagIdentifier.registerWellKnownTagName(TAG);
    }

    @Override
    public Component resolve(MarkupContainer container, MarkupStream markupStream, ComponentTag tag) {
        return new I18nMessage();
    }

    private static class I18nMessage extends WebComponent {

        public I18nMessage() {
            super("_i18n_");
        }

        @Override
        public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
            String message = markupStream.get().toCharSequence().toString();
            replaceComponentTagBody(markupStream, openTag, I18n.get(this.getClass()).tr(message));
        }
    }
}
