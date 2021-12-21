package wang.ismy.wolapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Title: Controller
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2021年12月21日 15:21
 */
@RestController
@RequestMapping("control")
public class Controller {

    /**
     * receive a hibernate request
     * @return
     */
    @RequestMapping("hibernate")
    public String hibernate() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Runtime.getRuntime().exec("shutdown -h");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return "success";
    }

    /**
     * receive a shutdown request
     * @return
     */
    @RequestMapping("shutdown")
    public String shutdown() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Runtime.getRuntime().exec("shutdown -s -t 0");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return "success";
    }
}
