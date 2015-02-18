package com.gentics.cailun.core.repository.action;

import java.util.List;

import com.gentics.cailun.core.rest.model.generic.GenericContent;

public interface ContentRepositoryActions<T extends GenericContent> {
	List<T> findCustomerNodeBySomeStrangeCriteria(Object strangeCriteria);
}
