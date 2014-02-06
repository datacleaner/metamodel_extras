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
package org.eobjects.sassy.metamodel;

import org.apache.metamodel.data.RowPublisher;
import org.apache.metamodel.schema.Column;
import org.eobjects.sassy.SasColumnType;
import org.eobjects.sassy.SasReaderCallback;

final class DataBuildingSasCallback implements SasReaderCallback {

	private final Column[] _columns;
	private final int _maxRows;
	private RowPublisher _publisher;

	public DataBuildingSasCallback(RowPublisher publisher, Column[] columns,
			int maxRows) {
		_publisher = publisher;
		_columns = columns;
		_maxRows = maxRows;
	}

	@Override
	public boolean readData() {
		return true;
	}

	@Override
	public void column(int columnIndex, String columnName, String columnLabel,
			SasColumnType columnType, int columnLength) {
		// do nothing
	}

	@Override
	public boolean row(int row, Object[] rowData) {
		if (_maxRows > 0 && row > _maxRows) {
			// don't read any more rows
			return false;
		}

		Object[] result = new Object[_columns.length];
		for (int i = 0; i < result.length; i++) {
			int columnNumber = _columns[i].getColumnNumber();
			result[i] = rowData[columnNumber];
		}

		_publisher.publish(result);

		return true;
	}

}
