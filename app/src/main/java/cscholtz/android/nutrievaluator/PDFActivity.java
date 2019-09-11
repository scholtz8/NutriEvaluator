package cscholtz.android.nutrievaluator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.File;


public class PDFActivity extends AppCompatActivity {
    private PDFView pdfView;
    private File file;
    private Uploader uploader;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pdf);

        uploader = new Uploader();
        pdfView = (PDFView) findViewById(R.id.pdfView);
        bundle = getIntent().getExtras();
        if(bundle!=null){
            file = new File(bundle.getString("path",""));
        }
        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pdf_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId() ){
            case R.id.upload_item:
                uploader.UploadFile(file);
                showToast("Uploaded");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private  void showToast(String text){
        Toast.makeText(PDFActivity.this, text, Toast.LENGTH_SHORT).show();
    }

}
