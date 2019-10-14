package cscholtz.android.nutrievaluator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {
    private Button loop,cache,normal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        normal = (Button) findViewById(R.id.buttonNormal);
        loop = (Button) findViewById(R.id.buttonLoop);
        cache = (Button) findViewById(R.id.buttonCache);

        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainMenuActivity.this, InputActivity.class);
                MainMenuActivity.this.startActivity(myIntent);
            }
        });
        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainMenuActivity.this, LoopUploadActivity.class);
                MainMenuActivity.this.startActivity(myIntent);
            }
        });
        cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainMenuActivity.this, CacheUploadActivity.class);
                MainMenuActivity.this.startActivity(myIntent);
            }
        });

    }
}
