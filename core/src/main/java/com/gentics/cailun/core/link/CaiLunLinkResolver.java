package com.gentics.cailun.core.link;

import org.apache.commons.lang.StringUtils;

import com.gentics.cailun.core.repository.generic.GlobalGenericContentRepository;
import com.gentics.cailun.core.rest.model.generic.GenericContent;

/**
 * Neo4j Cailun page resolver. This class will resolve cailun link placeholders.
 * 
 * @author johannes2
 *
 */
public class CaiLunLinkResolver extends AbstractLinkResolver {

	private GlobalGenericContentRepository<GenericContent> contentRepository;

	public CaiLunLinkResolver() {
		super(null);
	}

	public CaiLunLinkResolver(String text, GlobalGenericContentRepository<GenericContent> contentRepository) {
		super(text);
		this.contentRepository = contentRepository;
	}

	@Override
	public String call() throws Exception {
		String link = get();
		if (StringUtils.isEmpty(link)) {
			return "#";
		}
		int start = link.indexOf('(') + 1;
		int end = link.indexOf(')', start);
		// Extract page id
		String idString = link.substring(start, end);
		Long id = Long.valueOf(idString);
		return getPathForPageId(id);
	}

	/**
	 * Return the shortest path to the root node for this page.
	 * 
	 * @param id
	 * @return
	 */
	private String getPathForPageId(Long id) {
		// TODO exception handling
		// TODO use GenericContentUtils
		// return pageRepo.getPath(id);
		return "WIP";
	}

}
