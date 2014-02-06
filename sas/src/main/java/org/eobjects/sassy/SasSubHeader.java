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
 * Represents a "sub header" of a sas7bdat file.
 * 
 * @author Kasper Sørensen
 */
final class SasSubHeader {

	private final byte[] _rawData;
	private final byte[] _signatureData;

	public SasSubHeader(byte[] rawData, byte[] signatureData) {
		_rawData = rawData;
		_signatureData = signatureData;
	}

	public byte[] getSignatureData() {
		return _signatureData;
	}

	public byte[] getRawData() {
		return _rawData;
	}
}
