package com.github.ompc.greys.protocol.body;

import com.github.ompc.greys.protocol.Resp;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 获取版本应答
 * Created by oldmanpushcart@gmail.com on 15/11/7.
 */
@Type("get-version")
public class GetVersionResp extends Resp {

    // 服务端版本号
    @NotBlank
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}