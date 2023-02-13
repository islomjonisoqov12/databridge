package com.kdatalab.bridge.point;

import com.kdatalab.bridge.point.model.PointHistory;
import com.kdatalab.bridge.point.service.PointService;
import com.kdatalab.bridge.user.dto.UserDto;
import com.kdatalab.bridge.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PointController {

    private final UserService userService;

    private final PointService pointService;

    @RequestMapping(value = "/point-history", method = RequestMethod.GET)
    public ModelAndView pointHistory() throws Exception {

        ModelAndView mv = new ModelAndView("point/point-history.html");

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDto userInfo = null;

        try {
            if(principal != "anonymousUser") {
                UserDetails userDetails = (UserDetails) principal;
                userInfo = userService.getUserInfo(userDetails.getUsername());
            }
        } catch (Exception cce){
            DefaultOAuth2User auth2User = (DefaultOAuth2User) principal;
            userInfo = userService.getUserInfo(auth2User.getName());
        }

        String loginId = userInfo.getLoginId();
        loginId = "yanghee";// TODO after test this line should be removed
        List<PointHistory> pointHistory = pointService.getPointHistory(loginId);
        mv.addObject("pointHistory", pointHistory);

        return mv;
    }
}
