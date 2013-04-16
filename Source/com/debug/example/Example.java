/*
 * <<
 * XPage Debug Toolbar
 * Copyright 2012 Mark Leusink
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this 
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF 
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License
 * >> 
 */

package com.debug.example;

import com.debug.DebugToolbar;

public class Example {

	private DebugToolbar dBar;

	public Example() {
		System.out.println("Example() bean constructed");
		
		dBar = new DebugToolbar( this.getClass().toString() );
		dBar.info("Example message from the constructor of a bean");
		System.out.println("Should have an Example bean with a notification that the bean was constructed");
	}

	public void addErrorMessage(String msg) {
		dBar.error(msg);
	}

	public void addInfoMessage(String msg) {
		dBar.info(msg);
	}

	public void addWarningMessage(String msg) {
		System.out.println("addWarningMessages Example bean");
		dBar.warn("addWarningMessage: " + msg);
		someMethodForAnExample(true);
		someMethodForAnExample(false);
	}

	public boolean someMethodForAnExample(boolean returnedValue) {
		if (returnedValue) {
			dBar.debug("This is a debug message from a method, not from a button, return true");
		} else {
			dBar.debug("This is a debug message from a method, not from a button, return false");
		}
		return returnedValue;
	}
}
