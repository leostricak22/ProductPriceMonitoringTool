package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController {

    private final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    public void handleRegister() {
        logger.info("User registration");
    }

    public void handleLoginOpen() {
        SceneLoader.loadScene("login", "Login");
    }
}
