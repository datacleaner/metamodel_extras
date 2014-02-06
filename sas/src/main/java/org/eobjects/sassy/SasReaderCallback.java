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
 * A callback interface for the {@link SasReader} that allows a data "target" to
 * receive data and give instructions as to the further reading of a sas7bdat
 * file.
 * 
 * @author Kasper Sørensen
 */
public interface SasReaderCallback {

	/**
	 * Callback method that accepts a column discovered by the {@link SasReader}
	 * .
	 * 
	 * @param columnIndex
	 *            the index (0-based) of the column
	 * @param columnName
	 *            the physical name of the column
	 * @param columnLabel
	 *            the logical label of the column (often more user-friendly than
	 *            name)
	 * @param columnType
	 *            the type of the column
	 * @param columnLength
	 *            the length of the column
	 */
	public void column(int columnIndex, String columnName, String columnLabel,
			SasColumnType columnType, int columnLength);

	/**
	 * Should the reader read the data/rows (or only columns?)
	 * 
	 * @return true if data/rows should be read.
	 */
	public boolean readData();

	/**
	 * Callback method that accepts an array of row data.
	 * 
	 * @param rowNumber
	 *            the row number (1 = first row)
	 * @param rowData
	 *            the row data
	 * @return true if more rows should be read.
	 */
	public boolean row(int rowNumber, Object[] rowData);

}
