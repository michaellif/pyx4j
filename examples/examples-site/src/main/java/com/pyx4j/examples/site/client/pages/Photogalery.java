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


import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.examples.site.client.Page;
import com.pyx4j.examples.site.client.PhotoViewer;

/**
 * Introduction page.
 */
public class Photogalery extends Page {

	public static PageInfo init() {
		return new PageInfo("Photogalery") {

			public Page createInstance() {
				return new Photogalery();
			}
		};
	}

	public Photogalery() {

	}

	public Widget getContent() {
		String[] sImages = new String[] { "images/photo/image01.jpg",
				"images/photo/image02.jpg", "images/photo/image03.jpg",
				"images/photo/image04.jpg", "images/photo/image05.jpg",
				"images/photo/image06.jpg", "images/photo/image07.jpg",
				"images/photo/image08.jpg", "images/photo/image09.jpg",
				"images/photo/image10.jpg", "images/photo/image11.jpg",
				"images/photo/image12.jpg", "images/photo/image13.jpg",
				"images/photo/image14.jpg", "images/photo/image15.jpg",
				"images/photo/image16.jpg", "images/photo/image17.jpg",
				"images/photo/image18.jpg", "images/photo/image19.jpg",
				"images/photo/image20.jpg", "images/photo/image21.jpg",
				"images/photo/image22.jpg", "images/photo/image23.jpg",
				"images/photo/image24.jpg" };
		return new PhotoViewer(sImages);
	}

	public Widget getContentAdditions() {
		return new HTML("");
	}

	public Widget getNavigPanel() {
		return new HTML("");
	}

}
