package org.scaffoldeditor.scaffold.plugin_utils;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
/**
 * Classes marked as a scaffold plugin that implement PluginInitializer
 * will have their <code>initialize()</code> method called after the 
 * project has initialized.
 * @author Igrium
 *
 */
public @interface ScaffoldPlugin {

}
