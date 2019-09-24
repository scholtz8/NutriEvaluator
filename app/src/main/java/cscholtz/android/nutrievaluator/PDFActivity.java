package cscholtz.android.nutrievaluator;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


public class PDFActivity extends AppCompatActivity {
    private PDFView pdfView;
    private File file;
    Bundle bundle;
    private StorageReference storageReference;
    public int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pdf);

        storageReference = FirebaseStorage.getInstance().getReference();
        num = 0;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId() ){
            case R.id.upload_item:
                Compressor.zip( file.getName(),file.getName(),true);
                File f1 = new File(Environment.getExternalStorageDirectory().toString()+"/PDF/"+file.getName());
                Uri uri_file = Uri.fromFile(f1);
                StorageReference stg = storageReference.child(f1.getName());
                stg.putFile(uri_file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                num +=1;
                                if(num == 1){
                                    showToast("Archivo Uploaded");
                                }
                            }
                        });
                showToast("Uploaded");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private  void showToast(String text){
        Toast.makeText(PDFActivity.this, text, Toast.LENGTH_SHORT).show();
    }

}
