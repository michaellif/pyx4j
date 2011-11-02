var Cookie = {
	setCookie: function(name, value, expire) {
		// encode value
		value = encodeURIComponent(value);
		document.cookie = name + "=" + value + (expire ? "; expires=" + expire.toGMTString() : "");
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
				var value = document.cookie.substring(start, end);
				// enclosing double quotes must e stripped
				if (value.match(/^".*"$/)) {
					value = value.substring(1, value.length-1);
				}
				// return decoded value
				return decodeURIComponent(value);
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

function setSiteLocale(locale) {
	Cookie.setCookie('locale',locale);
}

var ClientPref = {
	myCookie: 'pmsitePref',
	prefMap: null,
	prefInit: function() {
		var prefStr = Cookie.getCookie(this.myCookie);
		if (prefStr.length > 0) {
			this.prefMap = {};
			var prefArr = prefStr.split(';');
			for (var i=0; i < prefArr.length; i++) {
				var nv = prefArr[i].split('=');
				if (nv.length > 1)
					this.prefMap[nv[0]] = nv[1];
			}
		}
	},
	getPref: function(name) {
		if (this.prefMap == null)
			this.prefInit();
		return this.prefMap[name];
	},
	setPref: function(name, value) {
		if (this.prefMap == null)
			this.prefInit();
		this.prefMap[name] = value;
		// save pref in the cookie
		var prefStr = '';
		for (var name in this.prefMap) {
			if (prefStr.length > 0)
				prefStr += ';';
			prefStr += name + '=' + value;
		}
		Cookie.setCookie(this.myCookie, prefStr);
	}
}