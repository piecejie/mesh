package com.gentics.mesh.core.rest.node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.gentics.mesh.core.rest.common.AbstractGenericRestResponse;
import com.gentics.mesh.core.rest.node.field.NodeField;
import com.gentics.mesh.core.rest.node.field.impl.NodeFieldImpl;
import com.gentics.mesh.core.rest.project.ProjectResponse;
import com.gentics.mesh.core.rest.schema.SchemaReference;
import com.gentics.mesh.core.rest.tag.TagReference;
import com.gentics.mesh.core.rest.user.NodeReference;
import com.gentics.mesh.parameter.NodeParameters;

/**
 * POJO for the node rest response model.
 */
public class NodeResponse extends AbstractGenericRestResponse {

	@JsonPropertyDescription("ISO 639-1 language tag of the node content.")
	private String language;

	@JsonPropertyDescription("List of languages for which content is available.")
	private List<String> availableLanguages;

	@JsonPropertyDescription("Map of webroot paths per language. This property will only be populated if the "
			+ NodeParameters.RESOLVE_LINKS_QUERY_PARAM_KEY + " query parameter has been set accordingly.")
	private Map<String, String> languagePaths;

	@JsonPropertyDescription("Reference to the parent node. Note that the root node of the project has no parent node.")
	private NodeReference parentNode;

	@JsonPropertyDescription("List of tags.")
	private List<TagReference> tags = new ArrayList<>();

	@JsonPropertyDescription("Reference to the project of the node.")
	private ProjectResponse project;

	/**
	 * Key: Schema name, Value: Node information
	 */
	@JsonPropertyDescription("Object which contains information about child elements.")
	private Map<String, NodeChildrenInfo> childrenInfo = new HashMap<>();

	@JsonPropertyDescription("Reference to the schema of the node.")
	private SchemaReference schema;

	@JsonPropertyDescription("Flag which indicates whether the node is a container and can contain nested elements.")
	private boolean isContainer;

	@JsonPropertyDescription("Display field value of the node. May not be retured if the node schema has no display field value")
	private String displayField;

	@JsonPropertyDescription("Dynamic map with fields of the node content.")
	private FieldMap fields = new FieldMapImpl();

	@JsonPropertyDescription("Webroot path to the node content. Will only be provided if the " + NodeParameters.RESOLVE_LINKS_QUERY_PARAM_KEY
			+ " query parameter has been set accordingly.")
	private String path;

	@JsonPropertyDescription("List of nodes which construct the breadcrumb. Note that the start node will not be included in the list.")
	private Deque<NodeReference> breadcrumb = new ArrayDeque<>();

	@JsonPropertyDescription("Reference to the version of the node content.")
	private VersionReference version;

	public NodeResponse() {
	}

	/**
	 * Return the parent node reference for the node. Note that no reference will be set for the base node of a project. This node has no parent. In those cases
	 * the reference will be set to null.
	 * 
	 * @return Node reference
	 */
	public NodeReference getParentNode() {
		return parentNode;
	}

	/**
	 * Set parent node reference
	 * 
	 * @param parentNode
	 *            Parent node reference
	 */
	public void setParentNode(NodeReference parentNode) {
		this.parentNode = parentNode;
	}

	/**
	 * Return the tags which were used to tag the node. The tags are nested within their tag families.
	 * 
	 * @return
	 */
	public List<TagReference> getTags() {
		return tags;
	}

	/**
	 * Set the schema reference of the node.
	 * 
	 * @param schema
	 */
	public void setSchema(SchemaReference schema) {
		this.schema = schema;
	}

	/**
	 * Return the schema reference of the node.
	 * 
	 * @return
	 */
	public SchemaReference getSchema() {
		return schema;
	}

	/**
	 * Return the children node info element.
	 * 
	 * @return
	 */
	public Map<String, NodeChildrenInfo> getChildrenInfo() {
		return childrenInfo;
	}

	/**
	 * Set the children node info element.
	 * 
	 * @param childrenInfo
	 */
	public void setChildrenInfo(Map<String, NodeChildrenInfo> childrenInfo) {
		this.childrenInfo = childrenInfo;
	}

	/**
	 * Return the flag which indicates whether the node is a container.
	 * 
	 * @return Container flag
	 */
	public boolean isContainer() {
		return isContainer;
	}

	/**
	 * Set the container flag which indicates whether the node is a container for other nodes. (eg. a folder)
	 * 
	 * @param isContainer
	 *            Container flag
	 */
	public void setContainer(boolean isContainer) {
		this.isContainer = isContainer;
	}

	/**
	 * Return the display field name for the node.
	 * 
	 * @return Display field
	 */
	public String getDisplayField() {
		return displayField;
	}

	/**
	 * Set the display field value for the node.
	 * 
	 * @param displayField
	 *            Display field
	 */
	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	/**
	 * Return the project to which the node belongs.
	 * 
	 * @return Project of the node
	 */
	public ProjectResponse getProject() {
		return project;
	}

	/**
	 * Set the project to which the node belongs.
	 * 
	 * @param project
	 *            Project of the node
	 */
	public void setProject(ProjectResponse project) {
		this.project = project;
	}

	/**
	 * Return a map with fields of the node.
	 * 
	 * @return Map with fields
	 */
	public FieldMap getFields() {
		return fields;
	}

	/**
	 * Return the language tag of the node.
	 * 
	 * @return Language tag
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Set the language of the node.
	 * 
	 * @param language
	 *            Language tag
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Return a list of language tags which are available for the node.
	 * 
	 * @return List of language tags
	 */
	public List<String> getAvailableLanguages() {
		return availableLanguages;
	}

	/**
	 * Set a list of language tags which are available for the node.
	 * 
	 * @param availableLanguages
	 *            List of language tags
	 */
	public void setAvailableLanguages(List<String> availableLanguages) {
		this.availableLanguages = availableLanguages;
	}

	/**
	 * Return the language webroot paths for the node. The map key is the language tag and the value is the resolved webroot link.
	 * 
	 * @return
	 */
	public Map<String, String> getLanguagePaths() {
		return languagePaths;
	}

	/**
	 * Set the language webroot paths.
	 * 
	 * @param languagePaths
	 */
	public void setLanguagePaths(Map<String, String> languagePaths) {
		this.languagePaths = languagePaths;
	}

	/**
	 * Get the webroot URL to the node
	 * 
	 * @return webroot URL
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Set the webroot path
	 * 
	 * @param path
	 *            webroot path
	 * @return Fluent API
	 */
	public NodeResponse setPath(String path) {
		this.path = path;
		return this;
	}

	/**
	 * Return the breadcrumb of the node. The key contains the uuid of the breadcrumb segment. The value maps to the segment name. The breadcrumb will only
	 * contain parent elements of the node for this response.
	 * 
	 * @return
	 */
	public Deque<NodeReference> getBreadcrumb() {
		return breadcrumb;
	}

	/**
	 * Set the breadcrumb for the node.
	 * 
	 * @param breadcrumb
	 * @return Fluent API
	 */
	public NodeResponse setBreadcrumb(Deque<NodeReference> breadcrumb) {
		this.breadcrumb = breadcrumb;
		return this;
	}

	/**
	 * Get the version of the fields
	 * 
	 * @return version number
	 */
	public VersionReference getVersion() {
		return version;
	}

	/**
	 * Set the version of the fields
	 * 
	 * @param version
	 * @return Fluent API
	 */
	public NodeResponse setVersion(VersionReference version) {
		this.version = version;
		return this;
	}

	/**
	 * Transform the node information into a field.
	 * 
	 * @return
	 */
	public NodeField toField() {
		NodeField field = new NodeFieldImpl();
		field.setPath(getPath());
		field.setUuid(getUuid());
		field.setLanguagePaths(getLanguagePaths());
		return field;
	}

	/**
	 * Create a node reference using the node.
	 * 
	 * @return
	 */
	public NodeReference toReference() {
		NodeReference reference = new NodeReference();
		reference.setUuid(getUuid());
		reference.setPath(getPath());
		reference.setSchema(getSchema());
		return reference;
	}

}