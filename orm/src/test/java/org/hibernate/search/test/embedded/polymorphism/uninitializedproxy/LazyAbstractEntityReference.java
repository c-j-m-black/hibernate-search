/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.test.embedded.polymorphism.uninitializedproxy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.search.annotations.DocumentId;

@Entity
@Cacheable
public class LazyAbstractEntityReference {

	@Id
	@GeneratedValue
	@DocumentId
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	private AbstractEntity entity;

	protected LazyAbstractEntityReference() {
		// For Hibernate use only
	}

	public LazyAbstractEntityReference(AbstractEntity entity) {
		super();
		setEntity( entity );
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public AbstractEntity getEntity() {
		return entity;
	}

	public void setEntity(AbstractEntity entity) {
		this.entity = entity;
	}
}
