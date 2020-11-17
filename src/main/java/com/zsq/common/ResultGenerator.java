package com.zsq.common;

import org.springframework.util.StringUtils;

public class ResultGenerator {

    //code
    public static final int RESULT_CODE_SUCCESS = 200;  // 成功处理请求
    public static final int RESULT_CODE_BAD_REQUEST = 412;  // 请求错误
    public static final int RESULT_CODE_NOT_LOGIN = 402;  // 未登录
    public static final int RESULT_CODE_PARAM_ERROR = 406;  // 传参错误
    public static final int RESULT_CODE_SERVER_ERROR = 500;  // 服务器错误

    //message
    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";
    private static final String DEFAULT_FAIL_MESSAGE = "FAIL";

    public static CommonResult success() {
        CommonResult result = new CommonResult();
        result.setCode(RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        return result;
    }

    public static CommonResult success(String message) {
        CommonResult result = new CommonResult();
        result.setCode(RESULT_CODE_SUCCESS);
        result.setMessage(message);
        return result;
    }

    public static CommonResult success(Object data) {
        CommonResult result = new CommonResult();
        result.setCode(RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        result.setData(data);
        return result;
    }

    public static CommonResult fail(String message) {
        CommonResult result = new CommonResult();
        result.setCode(RESULT_CODE_SERVER_ERROR);
        if (StringUtils.isEmpty(message)) {
            result.setMessage(DEFAULT_FAIL_MESSAGE);
        } else {
            result.setMessage(message);
        }
        return result;
    }


    public static CommonResult error(int code, String message) {
        CommonResult result = new CommonResult();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static CommonResult noResult(String message) {
        CommonResult result = new CommonResult();
        result.setCode(RESULT_CODE_BAD_REQUEST);
        result.setMessage(message);
        return result;
    }

}
