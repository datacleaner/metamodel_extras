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
 * A {@link SasReaderCallback} for getting the count of rows in a dataset.
 */
public class CountReaderCallback implements SasReaderCallback {

    private int count;

    /**
     * Constructs a {@link CountReaderCallback}
     */
    public CountReaderCallback() {
        count = 0;
    }

    @Override
    public void column(int columnIndex, String columnName, String columnLabel, SasColumnType columnType,
            int columnLength) {
        // do nothing
    }

    @Override
    public boolean readData() {
        return true;
    }

    @Override
    public boolean row(int rowNumber, Object[] rowData) {
        count++;
        return true;
    }

    /**
     * Gets the count of rows in the read table
     * 
     * @return
     */
    public int getCount() {
        return count;
    }

}
