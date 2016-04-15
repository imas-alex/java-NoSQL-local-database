package gdt.jgui.console;
/*
 * Copyright 2016 Alexander Imas
 * This file is part of JEntigrator.

    JEntigrator is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JEntigrator is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JEntigrator.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This interface describes the requester functionality.
 * @author imasa
 *
 */
public interface JRequester {
	/**
	 * The action tag.
	 */
	public static final String REQUESTER_ACTION="requester action";
	/**
	 * The response locator tag.
	 */
	public static final String REQUESTER_RESPONSE_LOCATOR="requester response locator";
   /**
    * Execute the response locator.
    * @param console the main console
    * @param  locator$ the response locator.
    */
	public void response(JMainConsole console,String locator$);
}
