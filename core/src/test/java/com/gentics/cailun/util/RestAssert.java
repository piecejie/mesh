package com.gentics.cailun.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map.Entry;

import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gentics.cailun.core.data.model.Content;
import com.gentics.cailun.core.data.model.Language;
import com.gentics.cailun.core.data.model.ObjectSchema;
import com.gentics.cailun.core.data.model.Project;
import com.gentics.cailun.core.data.model.Tag;
import com.gentics.cailun.core.data.model.auth.Group;
import com.gentics.cailun.core.data.model.auth.Role;
import com.gentics.cailun.core.data.model.auth.User;
import com.gentics.cailun.core.data.service.ContentService;
import com.gentics.cailun.core.data.service.LanguageService;
import com.gentics.cailun.core.rest.content.request.ContentCreateRequest;
import com.gentics.cailun.core.rest.content.response.ContentResponse;
import com.gentics.cailun.core.rest.group.request.GroupCreateRequest;
import com.gentics.cailun.core.rest.group.request.GroupUpdateRequest;
import com.gentics.cailun.core.rest.group.response.GroupResponse;
import com.gentics.cailun.core.rest.project.request.ProjectCreateRequest;
import com.gentics.cailun.core.rest.project.request.ProjectUpdateRequest;
import com.gentics.cailun.core.rest.project.response.ProjectResponse;
import com.gentics.cailun.core.rest.role.request.RoleCreateRequest;
import com.gentics.cailun.core.rest.role.response.RoleResponse;
import com.gentics.cailun.core.rest.schema.request.ObjectSchemaCreateRequest;
import com.gentics.cailun.core.rest.schema.response.ObjectSchemaResponse;
import com.gentics.cailun.core.rest.tag.response.TagResponse;
import com.gentics.cailun.core.rest.user.request.UserCreateRequest;
import com.gentics.cailun.core.rest.user.request.UserUpdateRequest;
import com.gentics.cailun.core.rest.user.response.UserResponse;

@Component
public class RestAssert {

	@Autowired
	private Neo4jTemplate neo4jTemplate;

	@Autowired
	private GraphDatabaseService graphDb;

	@Autowired
	private ContentService contentService;

	@Autowired
	private LanguageService languageService;

	public void assertGroup(Group group, GroupResponse restGroup) {
		assertEquals(group.getUuid(), restGroup.getUuid());
		assertEquals(group.getName(), restGroup.getName());
		// for (User user : group.getUsers()) {
		// assertTrue(restGroup.getUsers().contains(user.getUsername()));
		// }
		// TODO roles
		// group.getRoles()
		// TODO perms
	}

	public void assertGroup(GroupCreateRequest request, GroupResponse restGroup) {
		assertNotNull(request);
		assertNotNull(restGroup);
		assertEquals(request.getName(), restGroup.getName());
		assertNotNull(restGroup.getUsers());
		assertNotNull(restGroup.getUuid());
	}

	@Transactional
	public void assertUser(User user, UserResponse restUser) {
		assertNotNull("The user must not be null.", user);
		assertNotNull("The restuser must not be null", restUser);
		user = neo4jTemplate.fetch(user);
		assertEquals(user.getUsername(), restUser.getUsername());
		assertEquals(user.getEmailAddress(), restUser.getEmailAddress());
		assertEquals(user.getFirstname(), restUser.getFirstname());
		assertEquals(user.getLastname(), restUser.getLastname());
		assertEquals(user.getUuid(), restUser.getUuid());
		assertEquals(user.getGroups().size(), restUser.getGroups().size());
		// TODO groups
	}

	@Transactional
	public void assertTag(Tag tag, TagResponse restTag) {
		tag.setSchema(neo4jTemplate.fetch(tag.getSchema()));
		assertEquals(tag.getUuid(), restTag.getUuid());
		assertEquals(tag.getSchema().getUuid(), restTag.getSchema().getSchemaUuid());
		assertEquals(tag.getSchema().getName(), restTag.getSchema().getSchemaName());
	}

	/**
	 * Compare the create request with a content response.
	 * 
	 * @param request
	 * @param restContent
	 */
	public void assertContent(ContentCreateRequest request, ContentResponse restContent) {

		for (String languageTag : request.getProperties().keySet()) {
			for (Entry<String, String> entry : request.getProperties(languageTag).entrySet()) {
				assertEquals("The property {" + entry.getKey() + "} did not match with the response object property", entry.getValue(),
						restContent.getProperty(languageTag, entry.getKey()));
			}
		}

		String schemaName = request.getSchema().getSchemaName();
		assertEquals("The schemaname of the request does not match the response schema name", schemaName, restContent.getSchema().getSchemaName());
		assertEquals(request.getOrder(), restContent.getOrder());
		String tagUuid = request.getTagUuid();
		// TODO how to match the parent tag?

		assertNotNull(restContent.getUuid());
		assertNotNull(restContent.getCreator());
		assertNotNull(restContent.getPerms());

	}

	@Transactional
	public void assertContent(ContentCreateRequest request, Content content) {
		assertNotNull(request);
		assertNotNull(content);

		for (String languageTag : request.getProperties().keySet()) {
			for (Entry<String, String> entry : request.getProperties(languageTag).entrySet()) {
				Language language = languageService.findByLanguageTag(languageTag);
				String propValue = contentService.getProperty(content, language, entry.getKey());
				assertEquals("The property {" + entry.getKey() + "} did not match with the response object property", entry.getValue(), propValue);
			}
		}

		assertNotNull(content.getUuid());
		assertNotNull(content.getCreator());
	}

	@Transactional
	public void assertContent(Content content, ContentResponse readValue) {
		assertNotNull(content);
		assertNotNull(readValue);

		assertEquals(content.getOrder(), readValue.getOrder());
		assertNotNull(readValue.getPerms());

		ObjectSchema schema = content.getSchema();
		schema = neo4jTemplate.fetch(schema);
		assertNotNull("The schema of the test object should not be null. No further assertion can be verified.", schema);
		assertEquals(schema.getName(), readValue.getSchema().getSchemaName());
		assertEquals(schema.getUuid(), readValue.getSchema().getSchemaUuid());

		assertEquals(content.getUuid(), readValue.getUuid());

		assertNotNull(readValue.getCreator());

		// TODO match properties

	}

	public void assertRole(Role role, RoleResponse restRole) {
		assertNotNull(role);
		assertNotNull(restRole);
		assertEquals(role.getName(), restRole.getName());
		assertEquals(role.getUuid(), restRole.getUuid());
		assertNotNull(restRole.getPerms());
		assertNotNull(restRole.getGroups());
	}

	public void assertRole(RoleCreateRequest request, RoleResponse restRole) {
		assertNotNull(request);
		assertNotNull(restRole);
		assertEquals(request.getName(), restRole.getName());
		assertNotNull(restRole.getUuid());
		assertNotNull(restRole.getGroups());
	}

	public void assertProject(ProjectCreateRequest request, ProjectResponse restProject) {
		assertNotNull(request);
		assertNotNull(restProject);
		assertEquals(request.getName(), restProject.getName());
		assertNotNull(restProject.getUuid());
		assertNotNull(restProject.getPerms());
	}

	public void assertProject(Project project, ProjectResponse restProject) {
		assertNotNull(project);
		assertNotNull(restProject);
		assertNotNull(restProject.getUuid());
		assertNotNull(restProject.getPerms());
		assertNotNull(restProject.getRootTagUuid());
		assertEquals(project.getName(), restProject.getName());
		assertEquals(project.getUuid(), restProject.getUuid());
	}

	public void assertProject(ProjectUpdateRequest request, ProjectResponse restProject) {
		assertNotNull(request);
		assertNotNull(restProject);
		assertNotNull(restProject.getUuid());
		assertEquals(request.getName(), restProject.getName());
	}

	public void assertSchema(ObjectSchema schema, ObjectSchemaResponse restSchema) {
		assertNotNull(schema);
		assertNotNull(restSchema);
		assertEquals("Name does not match with the requested name.", schema.getName(), restSchema.getName());
		assertEquals("Description does not match with the requested description.", schema.getDescription(), restSchema.getDescription());
		assertEquals("Display names do not match.", schema.getDisplayName(), restSchema.getDisplayName());
		// TODO verify other fields
	}

	public void assertSchema(ObjectSchemaCreateRequest request, ObjectSchemaResponse restSchema) {
		assertNotNull(request);
		assertNotNull(restSchema);
		assertEquals("The name of the request schema and the name in the returned json do not match.", request.getName(), restSchema.getName());
		assertEquals("The description of the request and the returned json do not match.", request.getDescription(), restSchema.getDescription());
		assertEquals("The display name of the request and the returned json do not match.", request.getDisplayName(), restSchema.getDisplayName());
		// TODO assert for schema properties
	}

	public void assertUser(UserCreateRequest request, UserResponse restUser) {
		assertNotNull(request);
		assertNotNull(restUser);

		assertEquals(request.getUsername(), restUser.getUsername());
		assertEquals(request.getEmailAddress(), restUser.getEmailAddress());
		assertEquals(request.getLastname(), restUser.getLastname());
		assertEquals(request.getFirstname(), restUser.getFirstname());

		// TODO check groupuuid vs groups loaded user

	}

	public void assertUser(UserUpdateRequest request, UserResponse restUser) {
		assertNotNull(request);
		assertNotNull(restUser);

		if (request.getUsername() != null) {
			assertEquals(request.getUsername(), restUser.getUsername());
		}

		if (request.getEmailAddress() != null) {
			assertEquals(request.getEmailAddress(), restUser.getEmailAddress());
		}

		if (request.getLastname() != null) {
			assertEquals(request.getLastname(), restUser.getLastname());
		}

		if (request.getFirstname() != null) {
			assertEquals(request.getFirstname(), restUser.getFirstname());
		}
	}

	public void assertGroup(GroupUpdateRequest request, GroupResponse restGroup) {
		assertNotNull(request);
		assertNotNull(restGroup);

		if (request.getName() != null) {
			assertEquals(request.getName(), restGroup.getName());
		}

	}

}
