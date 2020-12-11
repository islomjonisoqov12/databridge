package com.kdatalab.bridge.join.service;

import com.kdatalab.bridge.join.dto.JoinDto;
import com.kdatalab.bridge.join.mapper.JoinMapper;
import com.kdatalab.bridge.point.dto.PointDto;
import com.kdatalab.bridge.point.service.PointService;
import com.kdatalab.bridge.user.dto.UserDto;
import com.kdatalab.bridge.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.List;

/**
 * 회원가입 Service
 * @author Enclouds
 * @since 2020.12.11
 */

@Service
public class JoinUserServiceImpl implements JoinUserService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JoinMapper joinMapper;

    @Autowired
    private PointService pointService;

    /**
     * 사용자 정보 조회
     *
     * @param loginId
     * @return
     * @throws Exception
     */
    @Override
    public UserDto getUserInfo(String loginId) throws Exception{
        UserDto params = new UserDto();
        params.setLoginId(loginId);

        UserDto userInfo = userMapper.selectUserInfo(params);
        return userInfo;
    }

    /**
     * 회원 가입 처리
     *
     * @param params
     * @throws Exception
     */
    @Override
    public void saveUserInfo(JoinDto params) throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        params.setPassword(passwordEncoder.encode(params.getPassword()));
        if(!StringUtils.isEmpty(params.getBirthDt())){
            params.setBirthDt(params.getBirthDt().replaceAll("-", ""));
        }

        //가입
        int result = joinMapper.saveUserInfo(params);
        //신규가입시 0Point 부여
        UserDto userDto = new UserDto();
        userDto.setLoginId(params.getLoginId());
        List<PointDto> pointDtoList = pointService.selectPointList(userDto);
        if(pointDtoList.size() < 0){
            joinMapper.saveUserPointInfo(params);
        }
    }
}
