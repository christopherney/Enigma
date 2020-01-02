package com.android.app.activity;

public class MyActivity extends Activity {

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.my_layout_id);

        final Button button = (Button) findViewById(R.id.my_cool_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your handler code here
            }
        }, true);

        this.fake_method(
                new MyListener() {
                    public void onResult(boolean success) {
                        System.out.println(success);
                    }
                },
                300,
                new MyErrorListener() {
                    public void onError(Exception error) {
                        System.out.println(error);
                    }
                },
                false
        );
    }
}
