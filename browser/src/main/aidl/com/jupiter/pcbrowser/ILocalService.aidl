// ILocalService.aidl
package com.jupiter.pcbrowser;

// Declare any non-default types here with import statements

interface ILocalService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int getPid();
    void restartServer();
    void startServer();
}
