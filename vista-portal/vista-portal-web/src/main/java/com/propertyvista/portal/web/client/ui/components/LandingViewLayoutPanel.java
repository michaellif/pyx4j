/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.components;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.propertyvista.portal.web.client.themes.LandingPagesTheme;
import com.propertyvista.portal.web.client.ui.residents.login.LandingViewImpl.LandingHtmlTemplates;

public class LandingViewLayoutPanel extends Composite {

    public interface Side {

        FlowPanel getHeader();

        FlowPanel getContent();

        FlowPanel getFooter();
    }

    private static class LandingSideSectionPanel extends FlowPanel {

        public LandingSideSectionPanel(String style) {
            setStyleName(style);
        }

    }

    private final LandingSideSectionPanel leftFooter;

    private final LandingSideSectionPanel rightFooter;

    private final LandingSideSectionPanel rightContent;

    private final LandingSideSectionPanel leftContent;

    private final LandingSideSectionPanel leftHeader;

    private final LandingSideSectionPanel rightHeader;

    public LandingViewLayoutPanel() {
        FlowPanel viewPanel = new FlowPanel();
        viewPanel.setStyleName(LandingPagesTheme.StyleName.LandingViewPanel.name());

        FlowPanel header = new FlowPanel();
        leftHeader = new LandingSideSectionPanel(LandingPagesTheme.StyleName.LandingViewSectionHeader.name());
        rightHeader = new LandingSideSectionPanel(LandingPagesTheme.StyleName.LandingViewSectionHeader.name());
        header.add(leftHeader);
        header.add(rightHeader);
        viewPanel.add(header);

        FlowPanel content = new FlowPanel();
        leftContent = new LandingSideSectionPanel(LandingPagesTheme.StyleName.LandingViewSectionContent.name());
        rightContent = new LandingSideSectionPanel(LandingPagesTheme.StyleName.LandingViewSectionContent.name());
        content.add(leftContent);
        content.add(rightContent);
        viewPanel.add(content);

        FlowPanel footer = new FlowPanel();
        leftFooter = new LandingSideSectionPanel(LandingPagesTheme.StyleName.LandingViewSectionFooter.name());
        rightFooter = new LandingSideSectionPanel(LandingPagesTheme.StyleName.LandingViewSectionFooter.name());
        footer.add(leftFooter);
        footer.add(rightFooter);
        viewPanel.add(footer);

        HTML orLine = makeOrLineDecoration();
        viewPanel.add(orLine);

        initWidget(viewPanel);
    }

    public Side getLeft() {
        return new Side() {
            @Override
            public FlowPanel getHeader() {
                return leftHeader;
            }

            @Override
            public FlowPanel getContent() {
                return leftContent;
            }

            @Override
            public FlowPanel getFooter() {
                return leftFooter;
            }

        };
    }

    public Side getRight() {
        return new Side() {

            @Override
            public FlowPanel getHeader() {
                return rightHeader;
            }

            @Override
            public FlowPanel getContent() {
                return rightContent;
            }

            @Override
            public FlowPanel getFooter() {
                return rightFooter;
            }

        };
    }

    private HTML makeOrLineDecoration() {
        HTML orLine = new HTML(LandingHtmlTemplates.TEMPLATES.orLineSeparator(LandingPagesTheme.StyleName.LandingOrLineSeparator.name()));
        return orLine;
    }

}
