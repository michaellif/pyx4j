var Cookie = {
	setCookie: function(name, value, expire) {
		document.cookie = name + "=" + escape(value) + (expire ? "; expires=" + expire.toGMTString() : "");
	},

	getCookie: function(Name) {
		var search = Name + "="
		if (document.cookie.length > 0) {
			start = document.cookie.indexOf(search);
			if (start != -1) {
				start += search.length;
				// set index of beginning of value
				end = document.cookie.indexOf(";", start); 
				// set index of end of cookie value
				if (end == -1) 
					end = document.cookie.length;
				return unescape(document.cookie.substring(start, end));
			} 
		}
	}
}

/*
 * Sets options for select field identified by selId
 */
function setSelectionOptions(selId, optArr, defOpt, selOpt) {
	var sel = document.getElementById(selId);
	if (! sel) return;
	sel.options.length = 0;
	if (defOpt) {
		sel.options[0] = new Option(defOpt,'');
	}
	for (var i = 0; i < optArr.length; i++) {
		var v = optArr[i];
		var selected = (v == selOpt);
		sel.options[sel.options.length] = new Option(v,v,selected,selected);
	}
}
/*
 * Sets src property for img element identified by imgId
 */
function setImgSrc(imgId, src) {
	var el_img = document.getElementById(imgId);
	el_img && el_img.setAttribute('src', src);
}

function switchSiteStyle() {
	var c = 'pmsiteStyle';
	var style = Cookie.getCookie(c);
	var newStyle = (parseInt(style) + 1) % 3;
	Cookie.setCookie(c, newStyle);
}

function setSiteLocale(locale) {
	Cookie.setCookie('locale',locale);
}