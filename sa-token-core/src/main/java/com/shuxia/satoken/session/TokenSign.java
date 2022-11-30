package com.shuxia.satoken.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Token 签名 Model
 * @author shuxia
 * @date 11/30/2022
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TokenSign implements Serializable {
    /**
     * Token 值
     */
    private String value;

    /**
     * 所属设备类型
     */
    private String device;


}
