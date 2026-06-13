package com.example.webmultiprocess.ipc;

import cc.suitalk.ipcinvoker.BaseIPCService;

public class MainProcessIPCService extends BaseIPCService {
    public static final String PROCESS_NAME = "com.example.webmultiprocess";

    @Override
    public String getProcessName() {
        return PROCESS_NAME;
    }
}
