package org.skycastle.client.shapes.loader

/**
 * Exception used if non-permitted class is loaded.
 */
class FilterException(forbiddenClass: String, source: String) extends Error("The class '"+forbiddenClass+"' is not permitted, but it was used in "+source) {

}
