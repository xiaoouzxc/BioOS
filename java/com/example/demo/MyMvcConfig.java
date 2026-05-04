package com.example.demo;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
	// ViewResolver 需要实现视图解析器接口的类
	@Bean
	public ViewResolver myViewResolver() {
		return new MyViewResolver();
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/index2").setViewName("index2");
		registry.addViewController("/index").setViewName("index");

		registry.addViewController("/index3").setViewName("index3");
	}

	// 自定义一个自己的视图解析器MyViewResolver
	public static class MyViewResolver implements ViewResolver {

		@Override
		public View resolveViewName(String s, Locale locale) throws Exception {
			return null;
		}

	}
}
