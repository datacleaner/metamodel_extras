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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.eobjects.sassy.IO;

import junit.framework.TestCase;

public class IOTest extends TestCase {

	public void testReadIntAndShortAndDouble() throws Exception {
		int size = 4 + 2 + 8;
		ByteBuffer bb = ByteBuffer.allocate(size);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		bb.putInt(12);
		bb.putShort((short) 3);
		bb.putDouble(562.3);

		byte[] bytes = bb.array();
		assertEquals(size, bytes.length);
		assertEquals(12, IO.readInt(bytes, 0));
		assertEquals(3, IO.readShort(bytes, 4));
		assertEquals(562.3, IO.readDouble(bytes, 6));
	}

	public void testGetBytes() throws Exception {
		byte[] bytes = new byte[] { 0, 2, 4, 6, 8, 10, 12 };

		byte[] result = IO.readBytes(bytes, 2, 3);
		assertTrue(Arrays.equals(new byte[] { 4, 6, 8 }, result));

		assertEquals(0, IO.readByte(bytes, 0));
		assertEquals(2, IO.readByte(bytes, 1));
	}

	public void testConcat() throws Exception {
		byte[] a1 = new byte[] { 0, 1, 2 };
		byte[] a2 = new byte[] { 3, 4 };
		byte[] res = IO.concat(a1, a2);

		assertEquals("[0, 1, 2, 3, 4]", Arrays.toString(res));
	}

	public void testReadInvalidBytes() throws Exception {
		try {
			IO.readBytes(IO.toBytes(1, 2, 3, 4), 3, 3);
			fail("Exception expected");
		} catch (SasReaderException e) {
			assertEquals("readBytes failed! data.length: 4, off: 3, len: 3",
					e.getMessage());
		}
	}
}
