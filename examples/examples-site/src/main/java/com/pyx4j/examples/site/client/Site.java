/*
 * Copyright 2007 Google Inc.
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
 */
package com.pyx4j.examples.site.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.examples.site.client.Page.PageInfo;
import com.pyx4j.examples.site.client.pages.AboutUs;
import com.pyx4j.examples.site.client.pages.ContactUs;
import com.pyx4j.examples.site.client.pages.Philosophy;
import com.pyx4j.examples.site.client.pages.Photogalery;
import com.pyx4j.examples.site.client.pages.Programs;
import com.pyx4j.widgets.client.GlassPanel;

/**
 * Application that demonstrates all of the built-in widgets.
 */
public class Site implements EntryPoint, ValueChangeHandler<String> {

	/**
	 * An image provider to make available images to Sinks.
	 */
	public interface SiteImageBundle extends ClientBundle {
		ImageResource curiousKidsImages();
	}

	private static final SiteImageBundle images = (SiteImageBundle) GWT
			.create(SiteImageBundle.class);

	protected SinkList list = new SinkList();

	private PageInfo curInfo;

	private Page curSink;

	private SimplePanel contentPanel;

	private SimplePanel navigPanel;

	private SimplePanel contentAdditionsPanel;

	private SimplePanel pageLabelTile;

	private DockPanel mainPanel;

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// Find the SinkInfo associated with the history context. If one is
		// found, show it (It may not be found, for example, when the user mis-
		// types a URL, or on startup, when the first context will be "").
		PageInfo info = list.find(event.getValue());
		if (info == null) {
			showHome();
			return;
		}
		show(info, false);
	}

	public void onModuleLoad() {
		init();
		DOM.setStyleAttribute(RootPanel.get("loading").getElement(), "display",
				"none");
	}

	private void init() {

		DOM.setStyleAttribute(RootPanel.get().getElement(), "backgroundImage",
				"url(images/frdbg1b.gif)");
		DOM.setStyleAttribute(RootPanel.get().getElement(),
				"backgroundPosition", "top left");
		DOM.setStyleAttribute(RootPanel.get().getElement(), "backgroundRepeat",
				"repeat");

		mainPanel = new DockPanel();
		DOM.setStyleAttribute(mainPanel.getElement(), "backgroundImage",
				"url(images/fr1bg1a.gif)");
		DOM.setStyleAttribute(mainPanel.getElement(), "backgroundPosition",
				"top left");
		DOM.setStyleAttribute(mainPanel.getElement(), "backgroundRepeat",
				"repeat");
		mainPanel.setHeight("100%");

		loadPages();

		Widget header = createHeader();
		mainPanel.add(header, DockPanel.NORTH);
		mainPanel.setCellHorizontalAlignment(header,
				HorizontalPanel.ALIGN_CENTER);

		mainPanel.add(list, DockPanel.NORTH);
		mainPanel.setCellHorizontalAlignment(list, DockPanel.ALIGN_CENTER);

		HorizontalPanel contentTopTile = new HorizontalPanel();

		pageLabelTile = new SimplePanel();
		contentTopTile.add(pageLabelTile);
		contentTopTile.setCellHorizontalAlignment(pageLabelTile,
				DockPanel.ALIGN_CENTER);
		contentTopTile.setWidth("100%");
		mainPanel.add(contentTopTile, DockPanel.NORTH);

		contentPanel = new SimplePanel();
		mainPanel.add(contentPanel, DockPanel.CENTER);
		contentPanel.setHeight("100%");
		contentPanel.setWidth("100%");
		mainPanel.setCellHeight(contentPanel, "100%");
		mainPanel.setCellWidth(contentPanel, "100%");

		HorizontalPanel poweredByTile = new HorizontalPanel();
		Label poweredByLabel = new Label("Powered by ");
		poweredByTile.add(poweredByLabel);
		poweredByTile.setCellVerticalAlignment(poweredByLabel,
				HorizontalPanel.ALIGN_MIDDLE);
		HTML poweredByLink = new HTML(
				"<a href=\"http://www.graphicgarden.com/\" target=\"blank\"><img src=\"images/ggarden.gif\" border=\"0\"></a>");
		poweredByTile.add(poweredByLink);
		poweredByTile.setCellVerticalAlignment(poweredByTile,
				HorizontalPanel.ALIGN_MIDDLE);
		mainPanel.add(poweredByTile, DockPanel.SOUTH);
		mainPanel.setCellHorizontalAlignment(poweredByTile,
				HorizontalPanel.ALIGN_CENTER);
		mainPanel.setCellVerticalAlignment(poweredByTile,
				HorizontalPanel.ALIGN_BOTTOM);
		mainPanel.setCellHeight(poweredByTile, "70px");

		Widget footer = createFooter();
		mainPanel.add(footer, DockPanel.SOUTH);
		DOM.setStyleAttribute(footer.getElement(), "marginTop", "20px");

		mainPanel.setCellHorizontalAlignment(footer,
				HorizontalPanel.ALIGN_CENTER);

		HorizontalPanel contentBottomTile = new HorizontalPanel();
		contentBottomTile.add(new Label(""));
		contentBottomTile.setWidth("100%");
		mainPanel.add(contentBottomTile, DockPanel.SOUTH);

		navigPanel = new SimplePanel();
		mainPanel.add(navigPanel, DockPanel.WEST);

		contentAdditionsPanel = new SimplePanel();
		mainPanel.add(contentAdditionsPanel, DockPanel.EAST);

		mainPanel.add(new UpdatePage(), DockPanel.EAST);

		mainPanel.setWidth("100%");

		History.addValueChangeHandler(this);

		AbsolutePanel contentPanel = new AbsolutePanel();

		contentPanel.add(GlassPanel.instance());
		contentPanel.add(mainPanel);

		RootPanel.get().add(contentPanel);

		// Show the initial screen.
		String initToken = History.getToken();
		if (initToken.length() > 0) {
			PageInfo info = list.find(initToken);
			if (info == null) {
				showHome();
				return;
			}
			show(info, false);
		} else {
			showHome();
		}
	}

	public void show(final PageInfo info, final boolean affectHistory) {
		GlassPanel.show();

		new Timer() {
			@Override
			public void run() {
				try {
					if (info != curInfo) {

						curInfo = info;
						curSink = info.getInstance();
						list.setTabSelection(info.getTabName());

						if (affectHistory) {
							History.newItem(info.getTabName());
						}

						Label pageLabel = new Label(info.getTabName());
						DOM.setStyleAttribute(pageLabel.getElement(),
								"fontSize", "24pt");
						DOM.setStyleAttribute(pageLabel.getElement(),
								"fontWeight", "bold");
						DOM.setStyleAttribute(pageLabel.getElement(), "color",
								"#859db6");
						DOM.setStyleAttribute(pageLabel.getElement(),
								"marginTop", "10pt");
						pageLabelTile.setWidget(pageLabel);
						contentPanel.setWidget(curSink.getContent());
						contentAdditionsPanel.setWidget(curSink
								.getContentAdditions());
						navigPanel.setWidget(curSink.getNavigPanel());
						curSink.onShow();
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
				GlassPanel.hide();
			}
		}.schedule(1);

	}

	public static SiteImageBundle getSiteImageBundle() {
		return images;
	}

	/**
	 * Adds all sinks to the list. Note that this does not create actual
	 * instances of all sinks yet (they are created on-demand). This can make a
	 * significant difference in startup time.
	 */
	protected void loadPages() {
		list.addPage(AboutUs.init(), 103, 121);
		list.addPage(Programs.init(), 206, 121);
		list.addPage(Philosophy.init(), 309, 121);
		list.addPage(Photogalery.init(), 412, 121);
		list.addPage(ContactUs.init(), 515, 121);

	}

	private void showHome() {
		show(list.find("About Us"), false);
	}

	private Widget createHeader() {
		HorizontalPanel header = new HorizontalPanel();
		Image headerImage = new Image(images.curiousKidsImages());
		headerImage.setVisibleRect(0, 0, 600, 60);
		header.add(headerImage);
		DOM.setStyleAttribute(header.getElement(), "marginTop", "20px");

		header.setWidth("600px");
		header.setHeight("60px");
		return header;
	}

	private Widget createFooter() {
		AbsolutePanel footer = new AbsolutePanel();

		Image footerImage = new Image(images.curiousKidsImages());
		footerImage.setVisibleRect(0, 60, 600, 60);
		footer.add(footerImage, 0, 0);

		Label testLabel = new Label("Copyright © 2007-2010 EduCity.");
		testLabel.setWidth("100%");
		footer.add(testLabel, 0, 20);

		footer.setWidth("600px");
		footer.setHeight("60px");

		return footer;
	}

}
