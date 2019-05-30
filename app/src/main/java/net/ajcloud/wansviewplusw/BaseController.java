package net.ajcloud.wansviewplusw;

import javax.annotation.PreDestroy;

public interface BaseController {

    @PreDestroy
    void Destroy();
}
