package at.htl.todo;
import android.app.Application;
import javax.inject.Singleton;
import dagger.hilt.android.HiltAndroidApp;
import android.util.Log;

@HiltAndroidApp
@Singleton
public class TodoApplication extends Application {

    static final String TAG = TodoApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "App started ...");
    }

}

