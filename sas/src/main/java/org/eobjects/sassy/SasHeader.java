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

/**
 * Represents the header metadata in the sas7bdat file format.
 * 
 * @author Kasper Sørensen
 */
final class SasHeader {

	private final String sasRelease;
	private final String sasHost;
	private final int pageSize;
	private final int pageCount;

	public SasHeader(String sasRelease, String sasHost, int pageSize,
			int pageCount) {
		this.sasRelease = sasRelease;
		this.sasHost = sasHost;
		this.pageSize = pageSize;
		this.pageCount = pageCount;
	}

	public String getSasRelease() {
		return sasRelease;
	}

	public String getSasHost() {
		return sasHost;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageCount() {
		return pageCount;
	}

	@Override
	public String toString() {
		return "SasHeader [sasRelease=" + sasRelease + ", sasHost=" + sasHost
				+ ", pageSize=" + pageSize + ", pageCount=" + pageCount + "]";
	}
}
