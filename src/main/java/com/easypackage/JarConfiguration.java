package com.easypackage;

import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Configures the project final jar, like the resources to be included or
 * excluded.
 * 
 * @author agusmunioz
 * 
 */
public class JarConfiguration {

	private String includes = "";

	private String excludes = "bin" + PathSeparator.get() + "**";

	/**
	 * Sets the list of regular expressions for including project resources in
	 * the final jar.
	 * 
	 * @param includes
	 *            a comma separated list of regular expressions.
	 */
	public void setIncludes(String includes) {
		this.includes = includes;
	}

	/**
	 * Sets the list of regular expressions for excluding project resources in
	 * the final jar.
	 * 
	 * @param excludes
	 *            a comma separated list of regular expressions.
	 */
	public void setExcludes(String excludes) {

		this.excludes += "," + excludes;
	}

	/**
	 * Configures resource inclusions and/or exclusions in the project final
	 * jar.
	 * 
	 * @param configuration
	 *            the plugin configuration.
	 */
	public void configure(Xpp3Dom configuration) {

		this.add(configuration, "includes", "include", this.includes);

		this.add(configuration, "excludes", "exclude", this.excludes);

	}

	/**
	 * Adds an specific configuration section in the jar plugin configuration.
	 * 
	 * @param configuration
	 *            plugin configuration as an {@link Xpp3Dom}.
	 * 
	 * @param element
	 *            name of the element to be added to the configuration that will
	 *            hold the list of resources.
	 * 
	 * @param child
	 *            name of child elements created for each resource expression.
	 * 
	 * @param resources
	 *            a comma separated list of resources regular expressions to be
	 *            added to the configuration.
	 */
	private void add(Xpp3Dom configuration, String element, String child,
			String resources) {

		if (resources.isEmpty()) {
			return;
		}

		String[] values = resources.split(",");

		Xpp3Dom domElement = new Xpp3Dom(element);

		for (String value : values) {

			Xpp3Dom domChild = new Xpp3Dom(child);
			domChild.setValue(PathUtils.osify(value.trim()));
			domElement.addChild(domChild);

		}

		configuration.addChild(domElement);
	}

}