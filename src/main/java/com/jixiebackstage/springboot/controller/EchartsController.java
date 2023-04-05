package com.jixiebackstage.springboot.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Quarter;
import com.jixiebackstage.springboot.common.Result;
import com.jixiebackstage.springboot.entity.Echarts;
import com.jixiebackstage.springboot.entity.User;
import com.jixiebackstage.springboot.service.IUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/echarts")
public class EchartsController {
    @Resource(name = "userServiceImpl")
    private IUserService userService;

    @GetMapping("/example")
    public Result get() {
        Map<String, Object> map = new HashMap<>();
        map.put("x", CollUtil.newArrayList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        map.put("y", CollUtil.newArrayList(150, 230, 224, 218, 135, 147, 260));
        return Result.success(map);
    }

    @GetMapping("/members")
    public Result members() {
        List<User> list = userService.list();
        int q1 = 0;
        int q2 = 0;
        int q3 = 0;
        int q4 = 0;
        for (User user : list) {
            Date createTime = user.getCreateTime();
            Quarter quarter = DateUtil.quarterEnum(createTime);
            switch (quarter) {
                case Q1:
                    q1 += 1;
                    break;
                case Q2:
                    q2 += 1;
                    break;
                case Q3:
                    q3 += 1;
                    break;
                case Q4:
                    q4 += 1;
                    break;
                default:
                    break;
            }
        }
        return Result.success(CollUtil.newArrayList(q1, q2, q3, q4));
    }


    @GetMapping("/speciality")
    public Result speciality() {
        List<Echarts> specialityList = new ArrayList<>();
        List<User> userList = userService.getList();
        Map<String, Integer> map = new HashMap<>();
        for (User user : userList) {
            String speciality = user.getSpeciality();
            if ("".equals(speciality) || speciality == null) {
                speciality = "未知";
            }
            if (!map.containsKey(speciality)) {
                map.put(speciality, 1);
            } else {
                map.put(speciality, map.get(speciality) + 1);
            }
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            specialityList.add(new Echarts(entry.getKey(), entry.getValue()));
        }
        return Result.success(specialityList);
    }
}
