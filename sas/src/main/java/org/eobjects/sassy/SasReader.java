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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A reader object that reads .sas7bdat files.
 * 
 * @author Kasper Sørensen
 */
public class SasReader {

	private static final Logger logger = LoggerFactory
			.getLogger(SasReader.class);

	// Subheader 'signatures'
	private static final byte[] SUBH_ROWSIZE = IO.toBytes(0xf7, 0xf7, 0xf7,
			0xf7);
	private static final byte[] SUBH_COLSIZE = IO.toBytes(0xf6, 0xf6, 0xf6,
			0xf6);
	private static final byte[] SUBH_COLTEXT = IO.toBytes(0xFD, 0xFF, 0xFF,
			0xFF);
	private static final byte[] SUBH_COLATTR = IO.toBytes(0xFC, 0xFF, 0xFF,
			0xFF);
	private static final byte[] SUBH_COLNAME = IO.toBytes(0xFF, 0xFF, 0xFF,
			0xFF);
	private static final byte[] SUBH_COLLABS = IO.toBytes(0xFE, 0xFB, 0xFF,
			0xFF);

	/**
	 * Magic number
	 */
	private static final byte[] MAGIC = IO.toBytes(0x0, 0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xc2, 0xea, 0x81, 0x60, 0xb3,
			0x14, 0x11, 0xcf, 0xbd, 0x92, 0x8, 0x0, 0x9, 0xc7, 0x31, 0x8c,
			0x18, 0x1f, 0x10, 0x11);

	private final File _file;

	public SasReader(File file) {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null");
		}
		_file = file;
	}

	public File getFile() {
		return _file;
	}

	protected static boolean isMagicNumber(int[] data) {
		return isMagicNumber(IO.toBytes(data));
	}

	protected static boolean isMagicNumber(byte[] data) {
		return isIdentical(data, MAGIC);
	}

	private static boolean isIdentical(byte[] data, byte[] expected) {
		if (data == null) {
			return false;
		}
		final byte[] comparedBytes;
		if (data.length > expected.length) {
			comparedBytes = Arrays.copyOf(data, expected.length);
		} else {
			comparedBytes = data;
		}
		return Arrays.equals(expected, comparedBytes);
	}

	public void read(SasReaderCallback callback) throws SasReaderException {
		FileInputStream is = null;
		try {
			is = new FileInputStream(_file);

			SasHeader header = readHeader(is);
			logger.info("({}) Header: {}", _file, header);

			readPages(is, header, callback);

			logger.info("({}) Done!", _file);
		} catch (Exception e) {
			if (e instanceof SasReaderException) {
				throw (SasReaderException) e;
			}
			throw new SasReaderException(
					"Unhandled exception occurred while reading sas7bdat file!",
					e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	private void readPages(FileInputStream is, SasHeader header,
			SasReaderCallback callback) throws Exception {
		final List<SasSubHeader> subHeaders = new ArrayList<SasSubHeader>();
		final List<Integer> columnOffsets = new ArrayList<Integer>();
		final List<Integer> columnLengths = new ArrayList<Integer>();
		final List<SasColumnType> columnTypes = new ArrayList<SasColumnType>();
		boolean subHeadersParsed = false;

		int rowCount = 0;

		final int pageSize = header.getPageSize();
		final int pageCount = header.getPageCount();

		// these variables will define the default amount of rows per page and
		// other defaults
		int row_count = -1;
		int row_count_fp = -1;
		int row_length = -1;
		int col_count = -1;

		for (int pageNumber = 0; pageNumber < pageCount; pageNumber++) {
			logger.info("({}) Reading page no. {}", _file, pageNumber);
			final byte[] pageData = new byte[pageSize];
			int read = is.read(pageData);
			if (read == -1) {
				// reached end of file
				break;
			}

			byte pageType = IO.readByte(pageData, 17);

			switch (pageType) {
			case 0:
			case 1:
			case 2:
				// accepted type
				logger.info("({}) page type supported: {}", _file, pageType);
				break;
			case 4:
				// accepted but not supported
				logger.info("({}) page type not fully supported: {}", _file,
						pageType);
				break;
			default:
				throw new SasReaderException("Page " + pageNumber
						+ " has unknown type: " + pageType);
			}

			if (pageType == 0 || pageType == 2) {
				// Read subheaders
				int subhCount = IO.readInt(pageData, 20);
				for (int subHeaderNumber = 0; subHeaderNumber < subhCount; subHeaderNumber++) {
					int base = 24 + subHeaderNumber * 12;

					int offset = IO.readInt(pageData, base);
					int length = IO.readInt(pageData, base + 4);

					if (length > 0) {
						byte[] rawData = IO.readBytes(pageData, offset, length);
						byte[] signatureData = IO.readBytes(rawData, 0, 4);
						SasSubHeader subHeader = new SasSubHeader(rawData,
								signatureData);
						subHeaders.add(subHeader);
					}
				}
			}

			if ((pageType == 1 || pageType == 2)) {

				if (!subHeadersParsed) {
					// Parse subheaders

					SasSubHeader rowSize = getSubHeader(subHeaders,
							SUBH_ROWSIZE, "ROWSIZE");
					row_length = IO.readInt(rowSize.getRawData(), 20);
					row_count = IO.readInt(rowSize.getRawData(), 24);
					int col_count_7 = IO.readInt(rowSize.getRawData(), 36);
					row_count_fp = IO.readInt(rowSize.getRawData(), 60);

					SasSubHeader colSize = getSubHeader(subHeaders,
							SUBH_COLSIZE, "COLSIZE");
					int col_count_6 = IO.readInt(colSize.getRawData(), 4);
					col_count = col_count_6;

					if (col_count_7 != col_count_6) {
						logger.warn(
								"({}) Column count mismatch: {} vs. {}",
								new Object[] { _file, col_count_6, col_count_7 });
					}

					SasSubHeader colText = getSubHeader(subHeaders,
							SUBH_COLTEXT, "COLTEXT");

					List<SasSubHeader> colAttrHeaders = getSubHeaders(
							subHeaders, SUBH_COLATTR, "COLATTR");
					final SasSubHeader colAttr;
					if (colAttrHeaders.isEmpty()) {
						throw new SasReaderException(
								"No column attribute subheader found");
					} else if (colAttrHeaders.size() == 1) {
						colAttr = colAttrHeaders.get(0);
					} else {
						colAttr = spliceColAttrSubHeaders(colAttrHeaders);
					}

					SasSubHeader colName = getSubHeader(subHeaders,
							SUBH_COLNAME, "COLNAME");

					List<SasSubHeader> colLabels = getSubHeaders(subHeaders,
							SUBH_COLLABS, "COLLABS");
					if (!colLabels.isEmpty() && colLabels.size() != col_count) {
						throw new SasReaderException(
								"Unexpected column label count ("
										+ colLabels.size() + ") expected 0 or "
										+ col_count);
					}

					for (int i = 0; i < col_count; i++) {
						int base = 12 + i * 8;

						final String columnName;
						byte amd = IO.readByte(colName.getRawData(), base);
						if (amd == 0) {
							int off = IO.readShort(colName.getRawData(),
									base + 2) + 4;
							int len = IO.readShort(colName.getRawData(),
									base + 4);
							columnName = IO.readString(colText.getRawData(),
									off, len);
						} else {
							columnName = "COL" + i;
						}

						// Read column labels
						final String label;
						if (colLabels != null && !colLabels.isEmpty()) {
							base = 42;
							byte[] rawData = colLabels.get(i).getRawData();
							int off = IO.readShort(rawData, base) + 4;
							short len = IO.readShort(rawData, base + 2);
							if (len > 0) {
								label = IO.readString(colText.getRawData(),
										off, len);
							} else {
								label = null;
							}
						} else {
							label = null;
						}

						// Read column offset, width, type (required)
						base = 12 + i * 12;

						int offset = IO.readInt(colAttr.getRawData(), base);
						columnOffsets.add(offset);

						int length = IO.readInt(colAttr.getRawData(), base + 4);
						columnLengths.add(length);

						short columnTypeCode = IO.readShort(
								colAttr.getRawData(), base + 10);
						SasColumnType columnType = (columnTypeCode == 1 ? SasColumnType.NUMERIC
								: SasColumnType.CHARACTER);
						columnTypes.add(columnType);

						if (logger.isDebugEnabled()) {
							logger.debug(
									"({}) column no. {} read: name={},label={},type={},length={}",
									new Object[] { _file, i, columnName, label,
											columnType, length });
						}
						callback.column(i, columnName, label, columnType,
								length);
					}

					subHeadersParsed = true;
				}

				if (!callback.readData()) {
					logger.info("({}) Callback decided to not read data", _file);
					return;
				}

				// Read data
				int row_count_p;
				int base;
				if (pageType == 2) {
					row_count_p = row_count_fp;
					int subhCount = IO.readInt(pageData, 20);
					base = 24 + subhCount * 12;
					base = base + base % 8;
				} else {
					row_count_p = IO.readInt(pageData, 18);
					base = 24;
				}

				if (row_count_p > row_count) {
					row_count_p = row_count;
				}

				for (int row = 0; row < row_count_p; row++) {
					Object[] rowData = new Object[col_count];
					for (int col = 0; col < col_count; col++) {
						int off = base + columnOffsets.get(col);
						int len = columnLengths.get(col);

						SasColumnType columnType = columnTypes.get(col);
						if (len > 0) {
							byte[] raw = IO.readBytes(pageData, off, len);
							if (columnType == SasColumnType.NUMERIC && len < 8) {
								ByteBuffer bb = ByteBuffer.allocate(8);
								for (int j = 0; j < 8 - len; j++) {
									bb.put((byte) 0x00);
								}
								bb.put(raw);
								raw = bb.array();

								// col$length <- 8
								len = 8;
							}

							final Object value;
							if (columnType == SasColumnType.CHARACTER) {
								String str = IO.readString(raw, 0, len);
								str = str.trim();
								value = str;
							} else {
								value = IO.readNumber(raw, 0, len);
							}
							rowData[col] = value;
						}
					}

					if (logger.isDebugEnabled()) {
						logger.debug("({}) row no. {} read: {}", new Object[] {
								_file, row, rowData });
					}

					rowCount++;
					boolean next = callback.row(rowCount, rowData);
					if (!next) {
						logger.info("({}) Callback decided to stop iteration",
								_file);
						return;
					}

					base = base + row_length;
				}
			}
		}
	}

	private SasSubHeader spliceColAttrSubHeaders(
			List<SasSubHeader> colAttrHeaders) {
		final int colAttrHeadersSize = colAttrHeaders.size();
		logger.info("({}) Splicing {} column attribute headers", _file,
				colAttrHeadersSize);

		byte[] result = IO.readBytes(colAttrHeaders.get(0).getRawData(), 0,
				colAttrHeaders.get(0).getRawData().length - 8);

		for (int i = 1; i < colAttrHeadersSize; i++) {
			byte[] rawData = colAttrHeaders.get(i).getRawData();
			result = IO.concat(result,
					IO.readBytes(rawData, 12, rawData.length - 20));
		}

		return new SasSubHeader(result, null);
	}

	private List<SasSubHeader> getSubHeaders(List<SasSubHeader> subHeaders,
			byte[] signature, String name) {
		List<SasSubHeader> result = new ArrayList<SasSubHeader>();
		for (SasSubHeader subHeader : subHeaders) {
			byte[] signatureData = subHeader.getSignatureData();
			if (isIdentical(signatureData, signature)) {
				result.add(subHeader);
			}
		}
		return result;
	}

	private SasSubHeader getSubHeader(List<SasSubHeader> subHeaders,
			byte[] signature, final String name) {
		List<SasSubHeader> result = getSubHeaders(subHeaders, signature, name);
		if (result.isEmpty()) {
			throw new SasReaderException("Could not find sub header: " + name);
		} else if (result.size() != 1) {
			throw new SasReaderException("Multiple (" + result.size()
					+ ") instances of the same sub header: " + name);
		}
		return result.get(0);
	}

	private SasHeader readHeader(InputStream is) throws Exception {
		byte[] header = new byte[1024];
		int read = is.read(header);
		if (read != 1024) {
			throw new SasReaderException(
					"Header too short (not a sas7bdat file?): " + read);
		}

		if (!isMagicNumber(header)) {
			throw new SasReaderException("Magic number mismatch!");
		}

		final int pageSize = IO.readInt(header, 200);
		if (pageSize < 0) {
			throw new SasReaderException("Page size is negative: " + pageSize);
		}

		final int pageCount = IO.readInt(header, 204);
		if (pageCount < 1) {
			throw new SasReaderException("Page count is not positive: "
					+ pageCount);
		}

		logger.info("({}) page size={}, page count={}", new Object[] { _file,
				pageSize, pageCount });

		final String sasRelease = IO.readString(header, 216, 8);
		final String sasHost = IO.readString(header, 224, 8);

		return new SasHeader(sasRelease, sasHost, pageSize, pageCount);
	}
}
