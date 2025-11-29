package com.example.sample.controller;

import com.example.sample.controller.vz0101.VZ0101Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ルートコントローラ
 */
@Controller
@RequestMapping("/")
public class RootController {
    
    /**
     * ホームページ
     *
     * @return ルートアクセス時のリダイレクト先
     */
    @GetMapping
    public String home() {
        return VZ0101Controller.REDIRECT;
    }
}
