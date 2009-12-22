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

import com.google.gwt.user.client.ui.Widget;

public abstract class Page {

	public abstract static class PageInfo {
		private Page instance;

		private String tabName;

		private String navigLinkName;

		public PageInfo(String tabName) {
			this.tabName = tabName;
		}

		public PageInfo(String tabName, String navigLinkName) {
			this.tabName = tabName;
			this.navigLinkName = navigLinkName;
		}

		public abstract Page createInstance();

		public final Page getInstance() {
			if (instance == null) {
				instance = createInstance();
			}
			return instance;
		}

		public String getTabName() {
			return tabName;
		}

		public String getNavigLinkName() {
			return navigLinkName;
		}


	}

	public abstract Widget getContent();

	public abstract Widget getContentAdditions();

	public abstract Widget getNavigPanel();

	public void onHide() {
		
	}
	
	public void onShow() {
		
	}
	
}
