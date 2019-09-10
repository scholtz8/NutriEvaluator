package cscholtz.android.nutrievaluator;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;


public class PDFActivity extends AppCompatActivity {
    private StorageReference storageReference;
    private PDFView pdfView;
    private File file;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pdf);
        storageReference = FirebaseStorage.getInstance().getReference();
        pdfView = (PDFView) findViewById(R.id.pdfView);
        bundle = getIntent().getExtras();
        if(bundle!=null){
            file = new File(bundle.getString("path",""));
            //showToast(bundle.getString("path",""));
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
                UploadFile();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UploadFile(){
        Uri fileu = Uri.fromFile(file);
        StorageReference stg = storageReference.child(file.getName());
        stg.putFile(fileu)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        showToast("File Uploaded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Uploading Error");
                    }
                });

    }

    private  void showToast(String text){
        Toast.makeText(PDFActivity.this, text, Toast.LENGTH_SHORT).show();
    }

}
