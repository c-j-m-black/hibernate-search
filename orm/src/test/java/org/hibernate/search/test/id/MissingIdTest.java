/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.test.id;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.Search;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;
import org.hibernate.search.exception.SearchException;
import org.hibernate.search.test.SearchTestBase;
import org.hibernate.search.testsupport.TestForIssue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Test that we throw explicit exceptions when we cannot retrieve the ID from documents.
 *
 * @author Yoann Rodiere
 */
@TestForIssue(jiraKey = "HSEARCH-2636")
public class MissingIdTest extends SearchTestBase {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void multipleResults_singleClass() throws Exception {
		EntityWithMissingIdWhenRetrievedFromIndex entity1 = new EntityWithMissingIdWhenRetrievedFromIndex();
		entity1.id = "1";
		EntityWithMissingIdWhenRetrievedFromIndex entity2 = new EntityWithMissingIdWhenRetrievedFromIndex();
		entity2.id = "2";

		Session s = openSession();
		Transaction tx = s.beginTransaction();
		s.save( entity1 );
		s.save( entity2 );
		tx.commit();
		s.clear();

		thrown.expect( SearchException.class );
		thrown.expectMessage( "HSEARCH000338" );
		thrown.expectMessage( "Incomplete entity information" );
		thrown.expectMessage( "'" + EntityWithMissingIdWhenRetrievedFromIndex.class.getName() + "'" );

		tx = s.beginTransaction();
		List<?> results = Search.getFullTextSession( s )
				.createFullTextQuery( new MatchAllDocsQuery(), EntityWithMissingIdWhenRetrievedFromIndex.class )
				.list();
		assertEquals( 2, results.size() );
		tx.commit();
		s.close();
	}

	@Override
	public Class<?>[] getAnnotatedClasses() {
		return new Class[] {
				EntityWithMissingIdWhenRetrievedFromIndex.class
		};
	}

	@Entity
	@Indexed
	private class EntityWithMissingIdWhenRetrievedFromIndex {
		@Id
		@FieldBridge(impl = MissingIdMockingFieldBridge.class)
		private String id;
	}

	public static class MissingIdMockingFieldBridge implements TwoWayFieldBridge {

		@Override
		public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
			luceneOptions.addFieldToDocument( name, (String) value, document );
		}

		@Override
		public Object get(String name, Document document) {
			return null; // Simulate a document without an ID field
		}

		@Override
		public String objectToString(Object object) {
			return (String) object;
		}

	}
}
