package com.mecoo.spider.exception;

/**
 * 浏览器操作相关异常
 */
public class BrowserException extends ScrapingException {
    
    public BrowserException(String message) {
        super(message);
    }
    
    public BrowserException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public BrowserException(Throwable cause) {
        super(cause);
    }
}