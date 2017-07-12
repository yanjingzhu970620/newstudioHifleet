package com.hifleet.utility;

/**
 * Created by du on 16/12/31.
 */

public interface  CompletionHandler {
    void complete(String retValue);
    void complete();
    void setProgressData(String value);
}
