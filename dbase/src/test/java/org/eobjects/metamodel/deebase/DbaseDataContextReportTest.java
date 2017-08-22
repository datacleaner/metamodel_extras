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
package org.eobjects.metamodel.deebase;

import java.util.List;

import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.query.Query;
import org.apache.metamodel.query.SelectItem;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;

import junit.framework.TestCase;

public class DbaseDataContextReportTest extends TestCase {

	private DbaseDataContext dc;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dc = new DbaseDataContext("src/test/resources/report.dbf");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		dc.close();
	}

	public void testExploreSchema() throws Exception {
		assertEquals("[information_schema, report.dbf]",
				dc.getSchemaNames().toString());

		Schema schema = dc.getSchemaByName("report.dbf");
		assertEquals("[Table[name=report,type=TABLE,remarks=null]]",
				schema.getTables().toString());

		Table table = schema.getTableByName("report");
		assertEquals(
				"[R_INST, R_NAME, R_TITLE, R_SUBTIT, CUS_CORP, CUS_NAME, CUS_TITL, CUS_SALU, CUS_AD1, CUS_AD2, CUS_AD3, CUS_CTY, CUS_ST, CUS_COU, CUS_ZIP, CUS_PHON, CUS_FAX, CUS_OPHN, CUS_EMAI, AUT_CORP, AUT_NAME, AUT_TITL, AUT_SALU, AUT_AD1, AUT_AD2, AUT_AD3, AUT_CTY, AUT_ST, AUT_COU, AUT_ZIP, AUT_PHON, AUT_FAX, AUT_OPHN, AUT_EMAI]",
				table.getColumnNames().toString());

		List<Column> columns = table.getColumnsOfType(ColumnType.VARCHAR);
		assertEquals("[]", columns.toString());

		columns = table.getColumnsOfType(ColumnType.CHAR);
		assertEquals(
				"[Column[name=R_INST,columnNumber=0,type=CHAR,nullable=null,nativeType=C,columnSize=4], Column[name=R_NAME,columnNumber=1,type=CHAR,nullable=null,nativeType=C,columnSize=22], Column[name=R_TITLE,columnNumber=2,type=CHAR,nullable=null,nativeType=C,columnSize=255], Column[name=R_SUBTIT,columnNumber=3,type=CHAR,nullable=null,nativeType=C,columnSize=64], Column[name=CUS_CORP,columnNumber=4,type=CHAR,nullable=null,nativeType=C,columnSize=55], Column[name=CUS_NAME,columnNumber=5,type=CHAR,nullable=null,nativeType=C,columnSize=55], Column[name=CUS_TITL,columnNumber=6,type=CHAR,nullable=null,nativeType=C,columnSize=55], Column[name=CUS_SALU,columnNumber=7,type=CHAR,nullable=null,nativeType=C,columnSize=55], Column[name=CUS_AD1,columnNumber=8,type=CHAR,nullable=null,nativeType=C,columnSize=33], Column[name=CUS_AD2,columnNumber=9,type=CHAR,nullable=null,nativeType=C,columnSize=33], Column[name=CUS_AD3,columnNumber=10,type=CHAR,nullable=null,nativeType=C,columnSize=33], Column[name=CUS_CTY,columnNumber=11,type=CHAR,nullable=null,nativeType=C,columnSize=22], Column[name=CUS_ST,columnNumber=12,type=CHAR,nullable=null,nativeType=C,columnSize=22], Column[name=CUS_COU,columnNumber=13,type=CHAR,nullable=null,nativeType=C,columnSize=22], Column[name=CUS_ZIP,columnNumber=14,type=CHAR,nullable=null,nativeType=C,columnSize=10], Column[name=CUS_PHON,columnNumber=15,type=CHAR,nullable=null,nativeType=C,columnSize=14], Column[name=CUS_FAX,columnNumber=16,type=CHAR,nullable=null,nativeType=C,columnSize=14], Column[name=CUS_OPHN,columnNumber=17,type=CHAR,nullable=null,nativeType=C,columnSize=14], Column[name=CUS_EMAI,columnNumber=18,type=CHAR,nullable=null,nativeType=C,columnSize=55], Column[name=AUT_CORP,columnNumber=19,type=CHAR,nullable=null,nativeType=C,columnSize=55], Column[name=AUT_NAME,columnNumber=20,type=CHAR,nullable=null,nativeType=C,columnSize=55], Column[name=AUT_TITL,columnNumber=21,type=CHAR,nullable=null,nativeType=C,columnSize=55], Column[name=AUT_SALU,columnNumber=22,type=CHAR,nullable=null,nativeType=C,columnSize=55], Column[name=AUT_AD1,columnNumber=23,type=CHAR,nullable=null,nativeType=C,columnSize=33], Column[name=AUT_AD2,columnNumber=24,type=CHAR,nullable=null,nativeType=C,columnSize=33], Column[name=AUT_AD3,columnNumber=25,type=CHAR,nullable=null,nativeType=C,columnSize=33], Column[name=AUT_CTY,columnNumber=26,type=CHAR,nullable=null,nativeType=C,columnSize=22], Column[name=AUT_ST,columnNumber=27,type=CHAR,nullable=null,nativeType=C,columnSize=22], Column[name=AUT_COU,columnNumber=28,type=CHAR,nullable=null,nativeType=C,columnSize=22], Column[name=AUT_ZIP,columnNumber=29,type=CHAR,nullable=null,nativeType=C,columnSize=10], Column[name=AUT_PHON,columnNumber=30,type=CHAR,nullable=null,nativeType=C,columnSize=14], Column[name=AUT_FAX,columnNumber=31,type=CHAR,nullable=null,nativeType=C,columnSize=14], Column[name=AUT_OPHN,columnNumber=32,type=CHAR,nullable=null,nativeType=C,columnSize=14], Column[name=AUT_EMAI,columnNumber=33,type=CHAR,nullable=null,nativeType=C,columnSize=55]]",
				columns.toString());
		assertEquals(columns.size(), table.getColumnCount());

		columns = table.getColumnsOfType(ColumnType.FLOAT);
		assertEquals("[]", columns.toString());

		columns = table.getColumnsOfType(ColumnType.DOUBLE);
		assertEquals("[]", columns.toString());

		columns = table.getColumnsOfType(ColumnType.DATE);
		assertEquals("[]", columns.toString());

		columns = table.getColumnsOfType(ColumnType.OTHER);
		assertEquals("[]", columns.toString());
	}

	public void testQuery() throws Exception {
		Schema schema = dc.getSchemaByName("report.dbf");
		Table table = schema.getTableByName("report");

		List<Column> columns = table.getColumns();
		Query q = new Query().select(columns).from(table);
		DataSet ds = dc.executeQuery(q);

		while (ds.next()) {
			Row row = ds.getRow();
			List<SelectItem> selectItems = row.getSelectItems();
			for (int i = 0; i < selectItems.size(); i++) {
				SelectItem selectItem = selectItems.get(i);
				Column column = columns.get(i);
				assertSame(selectItem.getColumn(), column);

				Object selectItemValue = row.getValue(selectItem);
				Object columnValue = row.getValue(column);
				assertEquals(selectItemValue, columnValue);

				assertNotNull(columnValue);

				assertTrue(columnValue instanceof Character
						|| columnValue instanceof String);
			}
		}
		ds.close();
	}
}