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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.schema.TableType;
import org.eobjects.sassy.SasReader;

/**
 * {@link Table} implementation for SAS data.
 * 
 * @author Kasper Sørensen
 */
final class SasTable extends MutableTable implements Serializable {

	private static final long serialVersionUID = 1L;

	private final File _file;
	private final List<Column> _columns;
	private boolean _columnsLoaded;

	public SasTable(File file, Schema schema) {
		super(createName(file), TableType.TABLE, schema);
		_file = file;
		_columns = new ArrayList<Column>();
		_columnsLoaded = false;
	}

	private static String createName(File file) {
		String name = file.getName();
		if (name.endsWith(".sas7bdat")) {
			name = name.substring(0, name.length() - 9);
		}
		return name;
	}

	public File getFile() {
		return _file;
	}

	@Override
	protected List<Column> getColumnsInternal() {
		if (!_columnsLoaded) {
			_columnsLoaded = true;
			SasReader sasReader = new SasReader(_file);
			sasReader.read(new ColumnBuildingSasCallback(this, _columns));
		}
		return _columns;
	}
}
