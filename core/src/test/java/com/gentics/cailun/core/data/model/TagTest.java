package com.gentics.cailun.core.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.Session;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.NotSupportedException;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.gentics.cailun.auth.CaiLunAuthServiceImpl;
import com.gentics.cailun.core.data.model.auth.User;
import com.gentics.cailun.core.data.service.ContentService;
import com.gentics.cailun.core.data.service.TagService;
import com.gentics.cailun.core.rest.tag.response.TagResponse;
import com.gentics.cailun.paging.PagingInfo;
import com.gentics.cailun.test.AbstractDBTest;
import com.gentics.cailun.util.JsonUtils;

public class TagTest extends AbstractDBTest {

	private static Logger log = LoggerFactory.getLogger(TagTest.class);

	public static final String GERMAN_NAME = "test german name";

	public static final String ENGLISH_NAME = "test english name";

	@Autowired
	private TagService tagService;

	@Autowired
	private ContentService contentService;

	@Before
	public void setup() throws Exception {
		setupData();
	}

	@Test
	public void testLocalizedFolder() {
		Language german = languageService.findByLanguageTag("de");

		Tag tag = new Tag();
		tagService.setName(tag, german, GERMAN_NAME);
		try (Transaction tx = graphDb.beginTx()) {
			tag = tagService.save(tag);
			tx.success();
		}
		assertNotNull(tag.getId());
		tag = tagService.findOne(tag.getId());
		assertNotNull("The folder could not be found.", tag);
		try (Transaction tx = graphDb.beginTx()) {
			String name = tagService.getName(tag, german);
			assertEquals("The loaded name of the folder did not match the expected one.", GERMAN_NAME, name);
			tx.success();
		}
	}

	@Test
	public void testContents() throws NotSupportedException {

		Tag tag = new Tag();

		Language english = languageService.findByLanguageTag("en");

		tagService.setName(tag, english, ENGLISH_NAME);
		try (Transaction tx = graphDb.beginTx()) {
			tag = tagService.save(tag);
			tx.success();
		}
		tag = tagService.findOne(tag.getId());
		assertNotNull(tag);

		final String GERMAN_TEST_FILENAME = "german.html";
		Content content = new Content();

		Language german = languageService.findByLanguageTag("de");

		try (Transaction tx = graphDb.beginTx()) {
			contentService.setFilename(content, german, GERMAN_TEST_FILENAME);
			contentService.setName(content, german, "german content name");
			content = contentService.save(content);
			tag.addContent(content);
			tag = tagService.save(tag);
			tx.success();
		}
		// Reload the tag and check whether the content was set
		try (Transaction tx = graphDb.beginTx()) {

			tag = tagService.reload(tag);
			assertEquals("The tag should have exactly one file.", 1, tag.getContents().size());
			Content contentFromTag = tag.getContents().iterator().next();
			assertNotNull(contentFromTag);
			assertEquals("We did not get the correct content.", content.getId(), contentFromTag.getId());
			String filename = contentService.getFilename(contentFromTag, german);
			assertEquals("The name of the file from the loaded tag did not match the expected one.", GERMAN_TEST_FILENAME, filename);

			// Remove the file/content and check whether the content was really removed
			assertTrue(tag.removeContent(contentFromTag));
			tag = tagService.save(tag);
			tx.success();
		}
		tag = tagService.reload(tag);
		assertEquals("The tag should not have any file.", 0, tag.getContents().size());

	}

	@Test
	public void testNodeTagging() {
		Language german = languageService.findByLanguageTag("de");

		// Create root with subfolder
		final String TEST_TAG_NAME = "testTag";

		Tag rootTag = new Tag();
		tagService.setName(rootTag, german, "wurzelordner");

		Tag subFolderTag = new Tag();
		tagService.setName(subFolderTag, german, TEST_TAG_NAME);
		subFolderTag = tagService.save(subFolderTag);

		rootTag.addTag(subFolderTag);
		tagService.save(rootTag);

		Tag reloadedNode = tagService.findOne(rootTag.getId());
		assertNotNull("The node shoule be loaded", reloadedNode);
		assertTrue("The test node should have a tag with the name {" + TEST_TAG_NAME + "}.", reloadedNode.hasTag(subFolderTag));

		Tag extraTag = new Tag();
		tagService.setName(extraTag, german, "extra ordner");
		assertFalse("The test node should have the random created tag.", reloadedNode.hasTag(extraTag));

		try (Transaction tx = graphDb.beginTx()) {
			assertTrue("The tag should be removed.", reloadedNode.removeTag(subFolderTag));
		}
	}

	@Test
	public void testFindAll() {

		User user = data().getUserInfo().getUser();
		List<String> languageTags = new ArrayList<>();
		languageTags.add("de");

		Page<Tag> page = tagService.findAllVisible(user, "dummy", languageTags, new PagingInfo(1, 10));
		assertEquals(11, page.getTotalElements());
		assertEquals(10, page.getSize());

		languageTags.add("en");
		page = tagService.findAllVisible(user, "dummy", languageTags, new PagingInfo(1, 14));
		assertEquals(16, page.getTotalElements());
		assertEquals(14, page.getSize());
	}

	@Test
	public void testTransformToRest() {
		Tag tag = data().getNews();
		assertNotNull("The UUID of the tag must not be null.", tag.getUuid());
		List<String> languageTags = new ArrayList<>();
		languageTags.add("en");
		languageTags.add("de");
		int depth = 3;

		RoutingContext rc = getMockedRoutingContext();
		for (int i = 0; i < 100; i++) {
			long start = System.currentTimeMillis();
			TagResponse response = tagService.transformToRest(rc, tag, languageTags, depth);
			assertNotNull(response);
			long dur = System.currentTimeMillis() - start;
			log.info("Transformation with depth {" + depth + "} took {" + dur + "} [ms]");
			System.out.println(JsonUtils.toJson(response));
		}
		// assertEquals(2, response.getChildTags().size());
		// assertEquals(4, response.getPerms().length);
	}

	private RoutingContext getMockedRoutingContext() {
		CaiLunAuthServiceImpl auth = springConfig.authService();

		// Create login session
		User user = data().getUserInfo().getUser();
		String loginSessionId = auth.createLoginSession(Long.MAX_VALUE, user);

		Session session = mock(Session.class);
		RoutingContext rc = mock(RoutingContext.class);
		when(rc.session()).thenReturn(session);
		when(session.getLoginID()).thenReturn(loginSessionId);
		return rc;
	}
}
