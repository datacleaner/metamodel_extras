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
package org.eobjects.metamodel.sas.metamodel;

import java.util.List;

import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.MutableColumn;
import org.apache.metamodel.schema.Table;
import org.eobjects.metamodel.sas.SasColumnType;
import org.eobjects.metamodel.sas.SasReaderCallback;

final class ColumnBuildingSasCallback implements SasReaderCallback {

	private final Table _table;
	private final List<Column> _columns;

	public ColumnBuildingSasCallback(Table table, List<Column> columns) {
		_table = table;
		_columns = columns;
	}

	@Override
	public boolean readData() {
		// don't read data
		return false;
	}

	@Override
	public void column(int columnIndex, String columnName, String columnLabel,
			SasColumnType columnType, int columnLength) {
		final ColumnType type;

		switch(columnType) {
			case NUMERIC:
				type = ColumnType.NUMERIC;
				break;
			case CHARACTER:
				type = ColumnType.VARCHAR;
				break;
			case DATE:
				type = ColumnType.DATE;
				break;
			case TIME:
				type = ColumnType.TIME;
				break;
			default:
				type = null;
		}

		MutableColumn column = new MutableColumn(columnName, type, _table,
				columnIndex, true);
		column.setRemarks(columnLabel);
		column.setColumnSize(columnLength);
		_columns.add(column);
	}

	@Override
	public boolean row(int row, Object[] rowData) {
		return false;
	}

}
