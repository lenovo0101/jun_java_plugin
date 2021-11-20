package com.huan.study.cloud.alibaba.user;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.huan.study.cloud.alibaba.user.feign.ProductFeignApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author huan.fu 2020/10/24 - 11:21
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;
    private final ProductFeignApi productFeignApi;

    @GetMapping("getAllProduct")
    public String getAllProduct() {
        return restTemplate.getForObject("http://product-provider/findAll", String.class);
    }

    @GetMapping("feign/getAllProduct")
    public String getAllProductFeign() {
        return productFeignApi.findAllProduct();
    }

    @GetMapping("/getProductProviderInstances")
    @SentinelResource(value = "getAllProductProviderInstances")
    public List<ServiceInstance> getProductProviderInstances() {
        return discoveryClient.getInstances("product-provider");
    }
}
