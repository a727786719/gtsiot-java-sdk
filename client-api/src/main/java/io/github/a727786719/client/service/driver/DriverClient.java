package io.github.a727786719.client.service.driver;

import io.github.a727786719.client.dto.ResponseDTO;
import io.github.a727786719.client.service.PlatformClient;
import io.github.a727786719.client.service.driver.dto.Command;

/**
 * 驱动管理客户端
 */
public interface DriverClient extends PlatformClient {

    /**
     * 发送指令
     *
     * @return 指令发送结果
     */
    ResponseDTO<String> sendCommand(Command command);

}
