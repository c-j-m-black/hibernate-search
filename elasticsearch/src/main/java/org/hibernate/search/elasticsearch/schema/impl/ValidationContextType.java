/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.elasticsearch.schema.impl;

enum ValidationContextType {

	INDEX,
	MAPPING,
	MAPPING_PROPERTY,
	MAPPING_PROPERTY_FIELD,
	ANALYZER,
	CHAR_FILTER,
	TOKENIZER,
	TOKEN_FILTER;

}