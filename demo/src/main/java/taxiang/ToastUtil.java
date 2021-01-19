package taxiang;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtil {
    private static Toast toast = null;

    @SuppressLint("ShowToast")
    public static void show(Context context, String text) {
        try {
            if (toast != null) {
                toast.setText(text);
            } else {
                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
            //解决在子线程中调用Toast的异常情况处理
            Looper.prepare();
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

}
