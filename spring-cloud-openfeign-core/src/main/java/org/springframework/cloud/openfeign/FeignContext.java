/*
 * Copyright 2013-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.openfeign;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.lang.Nullable;

/**
 * A factory that creates instances of feign classes. It creates a Spring
 * ApplicationContext per client name, and extracts the beans that it needs from there.
 *
 * @author Spencer Gibb
 * @author Dave Syer
 * @author Matt King
 * @author Jasbir Singh
 */
public class FeignContext extends NamedContextFactory<FeignClientSpecification> {

	/**
	 * 在通过 spring boot 自动配置的时候，spring-cloud 也提供了一个 Feign 的初始化配置类，FeignAutoConfiguration。它初始化了一个
	 *新的 FeignContext bean，并且把所有的 configuration 都放在 FeignContext 里面。
	 *
	 * FeignContext 继承了 NamedContextFactory，它会管理一批 Context，外部调用的时候会指定用哪个 context 来寻找对应的 bean，而 context
	 * 如果不存在，则会创建一个新的 AnnotationConfigApplicationContext。创建 context 的时候，会用 name 去匹配已有的 configurations（
	 * 加载该 FeignClient 注解里面提供的 configuration 属性类），如果有同名的，则就将该 configuration 注册进 context，另外如果有 default
	 * 开头的 configuration，也会将其注册到 context 里面，最后调用 refresh 方法对 context 进行初始化。初始化以后，相当于
	 * configuration 里面提供的encoder，decoder 这些就逗号了。
	 */
	public FeignContext() {
		super(FeignClientsConfiguration.class, "spring.cloud.openfeign", "spring.cloud.openfeign.client.name");
	}

	@Nullable
	public <T> T getInstanceWithoutAncestors(String name, Class<T> type) {
		try {
			return BeanFactoryUtils.beanOfType(getContext(name), type);
		}
		catch (BeansException ex) {
			return null;
		}
	}

	@Nullable
	public <T> Map<String, T> getInstancesWithoutAncestors(String name, Class<T> type) {
		return getContext(name).getBeansOfType(type);
	}

	public <T> T getInstance(String contextName, String beanName, Class<T> type) {
		return getContext(contextName).getBean(beanName, type);
	}

}
