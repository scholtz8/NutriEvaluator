package cscholtz.android.nutrievaluator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main2Activity extends AppCompatActivity {
    private Button loop,cache,normal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        normal = (Button) findViewById(R.id.buttonNormal);
        loop = (Button) findViewById(R.id.buttonLoop);
        cache = (Button) findViewById(R.id.buttonCache);

        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Main2Activity.this, LoopUploadActivity.class);
                Main2Activity.this.startActivity(myIntent);
            }
        });
        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Main2Activity.this, LoopUploadActivity.class);
                Main2Activity.this.startActivity(myIntent);
            }
        });
        cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Main2Activity.this, CacheUploadActivity.class);
                Main2Activity.this.startActivity(myIntent);
            }
        });

    }
}
