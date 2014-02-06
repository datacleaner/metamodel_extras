/**
 * eobjects.org SassyReader
 * Copyright (C) 2011 eobjects.org
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.eobjects.sassy;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A {@link FilenameFilter} that accepts only SAS dataset files (.sas7bdat).
 * 
 * @author Kasper Sørensen
 */
public class SasFilenameFilter implements FilenameFilter {

	public static boolean isSasDirectory(File directory) {
		if (directory == null) {
			return false;
		}
		if (!directory.isDirectory()) {
			return false;
		}
		String[] filenames = directory.list(new SasFilenameFilter());
		return filenames.length > 0;
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(".sas7bdat");
	}

}
