/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.elasticsearch.work.impl;

import org.hibernate.search.elasticsearch.impl.JestAPIFormatter;
import org.hibernate.search.elasticsearch.logging.impl.Log;
import org.hibernate.search.exception.AssertionFailure;
import org.hibernate.search.exception.SearchException;
import org.hibernate.search.util.logging.impl.LoggerFactory;

import com.google.gson.JsonObject;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.core.BulkResult.BulkResultItem;
import io.searchbox.core.DeleteByQuery;

/**
 * @author Yoann Rodiere
 */
public class DeleteByQueryWork extends SimpleElasticsearchWork<JestResult> {

	protected DeleteByQueryWork(Builder builder) {
		super( builder );
	}

	public static class Builder
			extends SimpleElasticsearchWork.Builder<Builder, JestResult> {
		private final DeleteByQuery.Builder jestBuilder;

		public Builder(String indexName, JsonObject payload) {
			super( indexName, DeleteByQueryResultAssessor.INSTANCE, NoopElasticsearchWorkSuccessReporter.INSTANCE );
			this.jestBuilder = new DeleteByQuery.Builder( payload.toString() )
					.addIndex( indexName );
		}

		public Builder type(String typeName) {
			jestBuilder.addType( typeName );
			return this;
		}

		@Override
		protected Action<JestResult> buildAction() {
			return jestBuilder.build();
		}

		@Override
		public DeleteByQueryWork build() {
			return new DeleteByQueryWork( this );
		}
	}

	private static class DeleteByQueryResultAssessor implements ElasticsearchRequestResultAssessor<JestResult> {

		private static final Log LOG = LoggerFactory.make( Log.class );

		private static final int NOT_FOUND_HTTP_STATUS_CODE = 404;

		public static final DeleteByQueryResultAssessor INSTANCE = new DeleteByQueryResultAssessor();

		private final DefaultElasticsearchRequestResultAssessor delegate;

		private DeleteByQueryResultAssessor() {
			this.delegate = DefaultElasticsearchRequestResultAssessor.builder( )
					.ignoreErrorStatuses( NOT_FOUND_HTTP_STATUS_CODE ).build();
		}

		@Override
		public void checkSuccess(ElasticsearchWorkExecutionContext context, Action<? extends JestResult> request, JestResult result) throws SearchException {
			this.delegate.checkSuccess( context, request, result );
			if ( result.getResponseCode() == NOT_FOUND_HTTP_STATUS_CODE ) {
				JestAPIFormatter formatter = context.getJestAPIFormatter();
				throw LOG.elasticsearchRequestDeleteByQueryNotFound( formatter.formatRequest( request ), formatter.formatResult( result ) );
			}
		}

		@Override
		public boolean isSuccess(ElasticsearchWorkExecutionContext context, BulkResultItem bulkResultItem) {
			throw new AssertionFailure( "This method should never be called, because DeleteByQuery actions are not Bulkable" );
		}
	}
}