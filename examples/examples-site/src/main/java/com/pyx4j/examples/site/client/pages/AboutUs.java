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
package com.pyx4j.examples.site.client.pages;



import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.examples.site.client.Page;

/**
 * Introduction page.
 */
public class AboutUs extends Page {

	public static PageInfo init() {
		return new PageInfo("About Us") {

			public Page createInstance() {
				return new AboutUs();
			}
		};
	}

	public AboutUs() {
	}

	public Widget getContent() {		
		HTML html = new HTML(StringUtils.unescapeHTML(DOM.getInnerHTML(RootPanel.get("AboutUs").getElement()), 0));
		DOM.setStyleAttribute(html.getElement(), "padding",
		"20px");
		html.setStyleName("site-content");
		return html;
	}

	public Widget getContentAdditions() {
		return new HTML("");
	}

	public Widget getNavigPanel() {
		return new HTML("");
	}

}
