/*
 * Copyright 2006-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.hadoop.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Core Spring Hadoop namespace handler
 *
 * @author Costin Leau
 */
class HadoopNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("tasklet", new HadoopTaskletParser());
		registerBeanDefinitionParser("job", new HadoopJobParser());
		registerBeanDefinitionParser("stream-job", new HadoopStreamJobParser());
		registerBeanDefinitionParser("configuration", new HadoopConfigParser());
		registerBeanDefinitionParser("resource-loader", new HadoopResourceLoaderParser());
		registerBeanDefinitionParser("pig", new PigParser());
		registerBeanDefinitionParser("hbase-configuration", new HbaseConfigurationParser());
		registerBeanDefinitionParser("hive-client", new HiveClientParser());
	}
}
