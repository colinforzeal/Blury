package application;

import android.app.Application;
import android.content.Context;

/*
    Class for holding context of application
    So we can get, for example, resources
    from everywhere.
 */
public class App extends Application{
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){return mContext;}
}
