package org.scaffoldeditor.scaffold.plugin_utils;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
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
