package io.github.microsphere.spring.cloud.context;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
public class User implements InitializingBean, DisposableBean {

    private String name;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("destroy...");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("init...");
    }
}
