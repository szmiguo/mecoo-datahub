package com.mecoo.spider.exception;

/**
 * Instagram抓取相关异常
 */
public class ScrapingException extends Exception {
    
    public ScrapingException(String message) {
        super(message);
    }
    
    public ScrapingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ScrapingException(Throwable cause) {
        super(cause);
    }
}