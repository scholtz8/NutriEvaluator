package cscholtz.android.nutrievaluator;


import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.Toolbar;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText nombreInput, edadInput, pesoInput, tallaInput, cinturaInput, caderaInput, braquialInput, carpoInput, tricipitalInput, bicipitalInput, suprailiacoInput, subescapularInput;
    private RadioGroup sexoGroup;
    private RadioButton sexoInput;
    private Button calcularButton;
    private String nombre, sexo,edad,peso,talla,cintura,cadera,braquial,carpo,tricipital,bicipital,suprailiaco,subescapular;//reciben los parametros de los inputs
    private TemplatePDF templatePDF;
    private SQLiteOpen_Helper helper = new SQLiteOpen_Helper(this,"BD1",null,1);
    String IMC,IPT,PESO_IDEAL,CMB,AMB,AGB,PT,CIN,RELCINCAD,CONTEXTURA; //reciben los string de texto a poner el el pdf

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombreInput = (EditText) findViewById(R.id.nombre);
        sexoGroup = (RadioGroup) findViewById(R.id.sexo);
        edadInput = (EditText) findViewById(R.id.edad);
        pesoInput = (EditText) findViewById(R.id.peso);
        tallaInput = (EditText) findViewById(R.id.talla);

        cinturaInput = (EditText) findViewById(R.id.cintura);
        caderaInput = (EditText) findViewById(R.id.cadera);
        braquialInput = (EditText) findViewById(R.id.braquial);
        carpoInput = (EditText) findViewById(R.id.carpo);

        tricipitalInput = (EditText) findViewById(R.id.tricipital);
        bicipitalInput = (EditText) findViewById(R.id.bicipital);
        suprailiacoInput  = (EditText) findViewById(R.id.suprailiaco);
        subescapularInput  = (EditText) findViewById(R.id.subescapular);

        calcularButton = (Button) findViewById(R.id.calcular_button);

        calcularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TomarInput();
                //InputEjemplo();
                if(!nombre.isEmpty() && !sexo.isEmpty() && !edad.isEmpty() && !peso.isEmpty() && !talla.isEmpty() && !cintura.isEmpty() && !cadera.isEmpty() && !braquial.isEmpty() && !carpo.isEmpty() && !tricipital.isEmpty() && !bicipital.isEmpty() && !suprailiaco.isEmpty() && !subescapular.isEmpty()){
                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    EvaluarDatos();
                    CreateTemplate();
                    pdfView();
                }else{
                    showToast("Llene todos los campos");
                }
            }
        });

    }
    public void TomarInput(){
        nombre = nombreInput.getText().toString();
        edad = edadInput.getText().toString();
        peso = pesoInput.getText().toString();
        talla = tallaInput.getText().toString();
        cintura = cinturaInput.getText().toString();
        cadera = caderaInput.getText().toString();
        braquial = braquialInput.getText().toString();
        carpo = carpoInput.getText().toString();
        tricipital = tricipitalInput.getText().toString();
        bicipital = bicipitalInput.getText().toString();
        suprailiaco = suprailiacoInput.getText().toString();
        subescapular = subescapularInput.getText().toString();
        int sexid = sexoGroup.getCheckedRadioButtonId();
        sexoInput = (RadioButton) findViewById(sexid);
        sexo = sexoInput.getText().toString();
    }

    public void InputEjemplo(){
        nombre = "Cristian";
        edad = "25";
        peso = "91.1";
        talla = "1.79";
        cintura ="110";
        cadera = "109";
        braquial = "33";
        carpo = "17";
        tricipital = "11";
        bicipital = "19";
        suprailiaco = "12";
        subescapular = "20";
        int sexid = sexoGroup.getCheckedRadioButtonId();
        sexoInput = (RadioButton) findViewById(sexid);
        sexo = sexoInput.getText().toString();
    }

    public void EvaluarDatos(){
        Evaluator E = new Evaluator(nombre, sexo, new Integer(edad),  new Integer(tricipital),  new Integer(bicipital),  new Integer(suprailiaco), new Integer(subescapular),  new Float(peso),  new Float(talla),  new Float(cintura),  new Float(cadera),  new Float(braquial),  new Float(carpo));
        helper.abrir();
        String e = helper.getIdEdad(new Integer(edad));
        IMC = "IMC: "+String.format("%.2f",E.getIMC()) + " kg/mt² "+E.evaluarIMC();
        IPT = "%IPT: "+String.format("%.2f",E.getIPT())+"% "+E.evaluarIPT();
        PESO_IDEAL = "PESO IDEAL: "+String.format("%.2f",E.getPesoIdeal())+" kg";
        Integer[] rCMB = E.rangoPercentiles(E.getCMB(),E.Percentiles(helper.percentiles(e,E.HoM(),"CMB")));
        CMB = "CMB: "+String.format("%.0f",E.getCMB())+ " mm (P"+rCMB[0]+"- P"+rCMB[1]+") "+E.evaluarPercentilesCMB(rCMB[0],rCMB[1]);
        Integer[] rAMB = E.rangoPercentiles(E.getAMB(),E.Percentiles(helper.percentiles(e,E.HoM(),"AMB")));
        AMB = "AMB: "+String.format("%.0f",E.getAMB())+ " mm (P"+rAMB[0]+"- P"+rAMB[1]+") "+E.evaluarPercentiles(rAMB[0],rAMB[1]);
        Integer[] rAGB = E.rangoPercentiles(E.getAGB(),E.Percentiles(helper.percentiles(e,E.HoM(),"AGB")));
        AGB = "AGB: "+String.format("%.0f",E.getAGB())+ " mm (P"+rAGB[0]+"- P"+rAGB[1]+") "+E.evaluarPercentiles(rAGB[0],rAGB[1]);
        Integer[] rPT = E.rangoPercentiles(E.getPT(),E.Percentiles(helper.percentiles(e,E.HoM(),"PT")));
        PT = "PT: "+String.format("%.0f",E.getPT())+" mm (P"+rPT[0]+"- P"+rPT[1]+") "+E.evaluarPercentiles(rPT[0],rPT[1]);
        CIN = "CINTURA: "+String.format("%.2f",E.getCin())+" cm "+E.evaluarCintura();
        RELCINCAD = "REL CINT/CAD: "+String.format("%.2f",E.getRelCinCad())+" "+E.evaluarRelCinCad();
        CONTEXTURA = "CONTEXTURA: "+String.format("%.2f",E.getContextura())+" "+E.evaluarContextura();
        helper.cerrar();
    }

    public void CreateTemplate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(new Date());
        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument(nombre+"_"+currentDate);
        templatePDF.addMetaData("Evaluacion Nutricional"+nombre,"evaluacion","cs");
        templatePDF.addTitles("Evaluacion Nutricional","Paciente: "+nombre,currentDate);
        templatePDF.addParagraph(IMC);
        templatePDF.addParagraph(IPT);
        templatePDF.addParagraph(PESO_IDEAL);
        templatePDF.addParagraph(CMB);
        templatePDF.addParagraph(AMB);
        templatePDF.addParagraph(AGB);
        templatePDF.addParagraph(PT);
        templatePDF.addParagraph(CIN);
        templatePDF.addParagraph(RELCINCAD);
        templatePDF.addParagraph(CONTEXTURA);
        templatePDF.closeDocument();
    }

    public void pdfView(){
        templatePDF.viewPDF();
    }

    private  void showToast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}
