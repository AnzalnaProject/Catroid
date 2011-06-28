/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.ist.tugraz.catroid.test.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

public class GetXListTest extends TestCase {
	private static final String[] DIRECTORIES = { "../catroid", "../catroidTest", "../catroidUiTest" };
	private static final String REGEX_PATTERN = "^.*get(Sprite|Script|Brick)List\\(\\)\\.add\\(.*$";

	public GetXListTest() {
	}

	private void traverseDirectory(File directory) throws IOException {
		File[] contents = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return (pathname.isDirectory() && !pathname.getName().equals("gen"))
						|| pathname.getName().endsWith(".java");
			}
		});

		for (File file : contents) {
			if (file.isDirectory()) {
				traverseDirectory(file);
			} else {
				checkFile(file);
			}
		}
	}

	private void checkFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		int lineCount = 1;
		String line = null;

		while ((line = reader.readLine()) != null) {
			assertFalse("File " + file.getName() + ":" + lineCount + " contains \"getScriptList().add()\"",
					line.matches(REGEX_PATTERN));
			++lineCount;
		}
	}

	public void testGetXListAddNotPresent() throws IOException {
		assertTrue("Pattern didn't match!", "getBrickList().add(new HideBrick(sprite))".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "getScriptList().add(virtualVariable)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "getSpriteList().add(VIRTUAL_08_VARIABLE)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "getBrickList().add(virtual_VAR14BLE_)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "getScriptList().add(_)".matches(REGEX_PATTERN));
		assertTrue("Pattern didn't match!", "foo(); getScriptList().add(42); bar();".matches(REGEX_PATTERN));
		assertFalse("Pattern matched! But shouldn't!", "getScriptList()add(MyVar)".matches(REGEX_PATTERN));
		assertFalse("Pattern matched! But shouldn't!", "getBrickList.add(MyVar)".matches(REGEX_PATTERN));

		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			traverseDirectory(directory);
		}
	}
}