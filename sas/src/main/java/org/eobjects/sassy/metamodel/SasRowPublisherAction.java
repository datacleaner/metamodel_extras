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
import org.apache.metamodel.util.Action;
import org.eobjects.sassy.SasReader;

public final  class SasRowPublisherAction implements Action<RowPublisher> {

	private final SasReader _sasReader;
	private final Column[] _columns;
	private final int _maxRows;

	public SasRowPublisherAction(SasReader sasReader, Column[] columns,
			int maxRows) {
		_sasReader = sasReader;
		_columns = columns;
		_maxRows = maxRows;
	}

	@Override
	public void run(RowPublisher publisher) throws Exception {
		_sasReader.read(new DataBuildingSasCallback(publisher, _columns,
				_maxRows));
		publisher.finished();
	}

}
