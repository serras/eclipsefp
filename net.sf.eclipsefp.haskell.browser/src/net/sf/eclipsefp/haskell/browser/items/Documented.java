package net.sf.eclipsefp.haskell.browser.items;

import org.json.JSONObject;

/**
 * Elements in tree that have documentation.
 * 
 * @author serras
 */

public abstract class Documented {
	String doc;

	public String getDoc() {
		return this.doc;
	}

	protected void setDoc(String doc) {
		this.doc = doc;
	}
	
	protected void setDoc(JSONObject o) {
		this.doc = o.optString("doc", "");
	}
}
