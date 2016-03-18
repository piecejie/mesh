package com.gentics.mesh.changelog.changes;

import com.gentics.mesh.changelog.AbstractChange;
import com.tinkerpop.blueprints.Vertex;

/**
 * Change which adds fancy moped.
 */
public class Change_424FA7436B6541269E6CE90C8C3D812D extends AbstractChange {

	@Override
	public String getUuid() {
		return getClass().getName().replaceAll("Change_", "");
	}

	@Override
	public String getName() {
		return "Add fancy moped";
	}

	@Override
	public void apply() {
		Vertex meshRootVertex = getMeshRootVertex();
		Vertex mopedVertex = getGraph().addVertex("TheMoped");
		mopedVertex.setProperty("name", "moped");
		meshRootVertex.addEdge("HAS_MOPED", mopedVertex);
		log.info("Added moped");
	}

	@Override
	public String getDescription() {
		return "Some changes to the graph that may take a while to execute";
	}

	@Override
	public boolean doesForceReindex() {
		return false;
	}

}