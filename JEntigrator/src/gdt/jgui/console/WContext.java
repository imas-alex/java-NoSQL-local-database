package gdt.jgui.console;

import gdt.data.store.Entigrator;

public interface WContext {
	public static final String WEB_HOME="web home";
	public static final String WEB_LOCATOR="web_locator";
	public static final String WEB_REQUESTER="web_requester";
	public static final String WEB_REQUESTER_URL="web_requester_url";
	public static final String WEB_TRANSFERABLE_PARAMETER="web_transferable_parameter";
	public static final String WEB_CONTEXT="web_context";
	public static final String BASES="bases";
	public static final String PAGE_METHOD="page method";
	public static final String PAGE_METHOD_DOWNLOAD="page method download";
	public static final String PAGE_METHOD_OPEN="page method open";
	public static final String PAGE_METHOD_SLIDESHOW="page method slideshow";
	public static final String ABOUT="whitepaper.html";
	public abstract String getWebView(Entigrator entigrator,String locator$);
	public abstract String getWebConsole(Entigrator entigrator,String locator$);
	
}
