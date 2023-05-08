package com.ayuan.service;

import com.ayuan.spring.Autowired;
import com.ayuan.spring.BeanNameAware;
import com.ayuan.spring.Component;
import com.ayuan.spring.InitializingBean;

/**
 * @author Ayuan
 * @Description: 登录测试
 * @date 2023/5/8 17:59
 */
@Component
public class LoginService implements BeanNameAware, InitializingBean {

    @Autowired
    private UserService userService;

    private String beanName;

    private String xxx;

    public void login() {
        System.out.println(userService.getName());
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        // ....
        System.out.println("afterPropertiesSet");
    }
}
