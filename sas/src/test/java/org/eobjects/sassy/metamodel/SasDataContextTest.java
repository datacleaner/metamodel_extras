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

import java.util.Arrays;
import java.util.List;

import javax.swing.table.TableModel;

import junit.framework.TestCase;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.MetaModelHelper;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.DataSetTableModel;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.query.Query;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;

public class SasDataContextTest extends TestCase {

	public void testPaging() throws Exception {
		DataContext dc = new SasDataContext("src/test/resources");

		Table table = dc.getDefaultSchema().getTableByName("mathattitudes");
		assertNotNull(table);

		Query q = dc.query().from(table).select(table.getColumns()).toQuery();
		q.setFirstRow(10);
		q.setMaxRows(10);

		assertEquals(
				"SELECT mathattitudes.CLASS, mathattitudes.STUDENT, mathattitudes.XAGE, mathattitudes.C2, mathattitudes.C5, mathattitudes.C7, mathattitudes.C13, mathattitudes.C18, mathattitudes.C21, mathattitudes.C38, mathattitudes.C39, mathattitudes.C42, mathattitudes.C44, mathattitudes.C46, mathattitudes.C51 FROM resources.mathattitudes",
				q.toSql());

		TableModel tm;
		tm = new DataSetTableModel(dc.executeQuery(q));
		assertEquals("1.0", tm.getValueAt(0, 0).toString());
		assertEquals("1.0", tm.getValueAt(1, 0).toString());
		assertEquals(10, tm.getRowCount());

		q.setFirstRow(50);
		tm = new DataSetTableModel(dc.executeQuery(q));
		assertEquals("2.0", tm.getValueAt(0, 0).toString());
		assertEquals(10, tm.getRowCount());
	}

	public void testResourcesFolder() throws Exception {
		DataContext dc = new SasDataContext("src/test/resources");

		assertEquals("[information_schema, resources]",
				Arrays.toString(dc.getSchemaNames()));

		Schema schema = dc.getDefaultSchema();

		assertEquals("Schema[name=resources]", schema.toString());
		assertEquals(8, schema.getTableCount());

		assertEquals(
				"[beef, charset_cyrillic_and_more, charset_mostly_latin, event2, mammals, mathattitudes, physeds2006, pizza]",
				Arrays.toString(schema.getTableNames()));

		Table table = schema.getTableByName("pizza");

		Row countRow = MetaModelHelper.executeSingleRowQuery(dc, dc.query()
				.from(table).selectCount().toQuery());
		assertEquals("Row[values=[300]]", countRow.toString());

		assertEquals("Table[name=pizza,type=TABLE,remarks=null]",
				table.toString());

		assertEquals("[id, mois, prot, fat, ash, sodium, carb, cal, brand]",
				Arrays.toString(table.getColumnNames()));

		assertEquals(
				"Column[name=brand,columnNumber=8,type=VARCHAR,nullable=true,nativeType=null,columnSize=1]",
				table.getColumnByName("brand").toString());
		assertEquals(
				"Column[name=id,columnNumber=0,type=VARCHAR,nullable=true,nativeType=null,columnSize=5]",
				table.getColumnByName("id").toString());
		assertEquals(
				"Column[name=mois,columnNumber=1,type=NUMERIC,nullable=true,nativeType=null,columnSize=8]",
				table.getColumnByName("mois").toString());

		Query q = dc.query().from(table).select("prot").and("brand").toQuery();
		q.setMaxRows(4);
		DataSet ds = dc.executeQuery(q);
		assertTrue(ds.next());
		assertEquals("Row[values=[21.43, a]]", ds.getRow().toString());
		assertTrue(ds.next());
		assertEquals("Row[values=[21.26, a]]", ds.getRow().toString());
		assertTrue(ds.next());
		assertEquals("Row[values=[19.99, a]]", ds.getRow().toString());
		assertTrue(ds.next());
		assertEquals("Row[values=[20.15, a]]", ds.getRow().toString());
		assertFalse(ds.next());

		ds.close();

		q = dc.query().from(table).select("brand")
				.orderBy(table.getColumnByName("brand")).toQuery();
		q.getSelectClause().setDistinct(true);
		List<Object[]> objectArrays = dc.executeQuery(q).toObjectArrays();
		assertEquals(10, objectArrays.size());
		assertEquals("a", objectArrays.get(0)[0]);
		assertEquals("b", objectArrays.get(1)[0]);
		assertEquals("c", objectArrays.get(2)[0]);
		assertEquals("d", objectArrays.get(3)[0]);
	}
}
