package taxiang;

import android.app.Application;


import cn.jiguang.verifysdk.api.JVerificationInterface;

public class App extends Application {

    private static App SApp;

    public static App getApp() {
        return SApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SApp = this;

        JVerificationInterface.setDebugMode(true);
        JVerificationInterface.init(this);

    }
}
