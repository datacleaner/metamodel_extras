/**
 * eobjects.org MetaModel
 * Copyright (C) 2010 eobjects.org
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
package org.eobjects.metamodel.access;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.MetaModelException;
import org.apache.metamodel.QueryPostprocessDataContext;
import org.apache.metamodel.data.CachingDataSetHeader;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.DataSetHeader;
import org.apache.metamodel.data.DefaultRow;
import org.apache.metamodel.data.InMemoryDataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.query.FilterItem;
import org.apache.metamodel.query.SelectItem;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.ColumnTypeImpl;
import org.apache.metamodel.schema.MutableColumn;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.schema.TableType;
import org.apache.metamodel.util.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.IndexData.ColumnDescriptor;

/**
 * DataContext implementation for MS Access database files.
 */
public final class AccessDataContext extends QueryPostprocessDataContext {

    private static final Logger logger = LoggerFactory.getLogger(AccessDataContext.class);

    private final File _file;
    private Database _database;

    public AccessDataContext(File file) {
        _file = file;
    }

    public AccessDataContext(String filename) {
        this(new File(filename));
    }

    private Database getDatabase() {
        if (_database == null) {
            synchronized (this) {
                if (_database == null) {
                    try {
                        _database = Database.open(_file, true);
                    } catch (IOException e) {
                        throw new MetaModelException(e);
                    }
                }
            }
        }
        return _database;
    }

    @Override
    protected Schema getMainSchema() throws MetaModelException {
        MutableSchema schema = new MutableSchema(_file.getName());
        Database db = getDatabase();
        for (com.healthmarketscience.jackcess.Table mdbTable : db) {
            final MutableTable table = new MutableTable(mdbTable.getName(), TableType.TABLE, schema);

            try {
                int i = 0;
                for (com.healthmarketscience.jackcess.Column mdbColumn : mdbTable.getColumns()) {
                    final ColumnType columnType = ColumnTypeImpl.convertColumnType(mdbColumn.getSQLType());
                    final MutableColumn column = new MutableColumn(mdbColumn.getName(), columnType, table, i, null);
                    column.setColumnSize((int) mdbColumn.getLength());
                    column.setNativeType(mdbColumn.getType().name());

                    table.addColumn(column);
                    i++;
                }

                try {
                    final Index primaryKeyIndex = mdbTable.getPrimaryKeyIndex();
                    final List<ColumnDescriptor> columnDescriptors = primaryKeyIndex.getColumns();
                    for (ColumnDescriptor columnDescriptor : columnDescriptors) {
                        final String name = columnDescriptor.getColumn().getName();
                        final MutableColumn column = (MutableColumn) table.getColumnByName(name);
                        column.setPrimaryKey(true);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to get PK index info for table: {}", mdbTable, e);
                }

                schema.addTable(table);

            } catch (Exception e) {
                throw new MetaModelException(e);
            }
        }
        return schema;
    }

    @Override
    protected String getMainSchemaName() throws MetaModelException {
        return _file.getName();
    }

    @Override
    protected Number executeCountQuery(Table table, List<FilterItem> whereItems, boolean functionApproximationAllowed) {
        try {
            com.healthmarketscience.jackcess.Table mdbTable = getDatabase().getTable(table.getName());
            return mdbTable.getRowCount();
        } catch (Exception e) {
            throw new MetaModelException(e);
        }
    }

    @Override
    public DataSet materializeMainSchemaTable(Table table, Column[] columns, int maxRows) {
        try {
            final com.healthmarketscience.jackcess.Table mdbTable = getDatabase().getTable(table.getName());
            final SelectItem[] selectItems = new SelectItem[columns.length];
            for (int i = 0; i < columns.length; i++) {
                selectItems[i] = new SelectItem(columns[i]);
            }

            final DataSetHeader header = new CachingDataSetHeader(selectItems);

            int rowNum = 0;
            final List<Row> data = new LinkedList<Row>();
            final Iterator<Map<String, Object>> it = mdbTable.iterator();
            while (it.hasNext() && (maxRows < 0 || rowNum < maxRows)) {
                rowNum++;
                final Map<String, Object> valueMap = it.next();
                final Object[] values = new Object[columns.length];
                for (int j = 0; j < columns.length; j++) {
                    values[j] = valueMap.get(columns[j].getName());
                }
                data.add(new DefaultRow(header, values));
            }

            return new InMemoryDataSet(header, data);
        } catch (Exception e) {
            throw new MetaModelException(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        _database.close();
    }
}