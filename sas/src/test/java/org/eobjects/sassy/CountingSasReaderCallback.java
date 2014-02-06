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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.query.SelectItem;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
class CountingSasReaderCallback implements SasReaderCallback {

	private static final Logger logger = LoggerFactory
			.getLogger(CountingSasReaderCallback.class);

	public static final int SAMPLE_LIMIT = 100;

	private final List<Object[]> sampleRows = new ArrayList<Object[]>();
	private final boolean readData;
	private final DataSet compareToDataSet;
	private int columnCount = 0;
	private int rowCount = 0;

	public CountingSasReaderCallback(boolean readData, DataSet compareToDataSet) {
		this.readData = readData;
		this.compareToDataSet = compareToDataSet;
	}

	@Override
	public boolean readData() {
		return readData;
	}

	@Override
	public void column(int columnIndex, String columnName, String columnLabel,
			SasColumnType columnType, int columnLength) {
		columnCount++;
		if (compareToDataSet != null) {
			SelectItem selectItem = compareToDataSet.getSelectItems()[columnIndex];
			Assert.assertEquals(selectItem.getColumn().getName(), columnName);
		}
	}

	@Override
	public boolean row(int row, Object[] rowData) {
		logger.info("row no. {}: {}", row, rowData);
		rowCount++;

		if (rowCount < SAMPLE_LIMIT) {
			sampleRows.add(rowData);
		}

		Assert.assertEquals(columnCount, rowData.length);

		if (compareToDataSet != null) {
			Assert.assertTrue(compareToDataSet.next());
			Object[] benchValues = compareToDataSet.getRow().getValues();
			Assert.assertEquals(benchValues.length, rowData.length);
			for (int i = 0; i < benchValues.length; i++) {
				Object benchValue = benchValues[i];
				if (".".equals(benchValue)) {
					// TODO: Should we accept NaN or instead return null?
					benchValue = Double.NaN;
				}
				Object actualValue = rowData[i];
				if (actualValue instanceof Number) {
					try {
						benchValue = Double.parseDouble(benchValues[i]
								.toString());
					} catch (NumberFormatException e) {
						logger.error("Could not parse {} as number",
								benchValues[i]);
					}
				}

				if (!benchValue.equals(actualValue)) {
					logger.error("Bench row:  \"{}\"",
							Arrays.toString(benchValues));
					logger.error("Actual row: \"{}\"", Arrays.toString(rowData));
					logger.error("Failing column: {}, {}", i, compareToDataSet
							.getSelectItems()[i].getColumn().toString());
					Assert.assertEquals(
							"Bench and actual values does not match: "
									+ benchValue + " vs. " + actualValue,
							benchValue.toString(), actualValue.toString());
				}

				benchValues[i] = benchValue;
			}
		}
		return true;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public int getRowCount() {
		return rowCount;
	}

	public List<Object[]> getSampleRows() {
		return sampleRows;
	}
}
