package com.ymnet.onekeyclean.cleanmore.root.shell;


import com.ymnet.onekeyclean.cleanmore.root.exception.NoAuthorizationException;

import java.io.BufferedReader;
import java.io.DataOutputStream;

/**
 * Created with IntelliJ IDEA.
 * Date: 15/8/3
 * Author: zhangcm
 */
interface ShellRunnable {
    void execute(Process proc, BufferedReader in, DataOutputStream out) throws NoAuthorizationException;
}
